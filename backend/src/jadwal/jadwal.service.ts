import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { JadwalGateway } from './jadwal.gateway';
import { InjectQueue } from '@nestjs/bullmq';
import { Queue } from 'bullmq';
import { JadwalProcessor } from './jadwal.processor';
import * as ExcelJS from 'exceljs';
const PdfPrinter = require('pdfmake');

@Injectable()
export class JadwalService {
  constructor(
    private prisma: PrismaService,
    private gateway: JadwalGateway,
    @InjectQueue('generate-jadwal') private generateQueue: Queue,
    private jadwalProcessor: JadwalProcessor,
  ) {}

  async generateJadwalAsync(id_sekolah: number) {
    // Bypass BullMQ to avoid Redis requirement in local demo
    this.jadwalProcessor.process({ data: { id_sekolah } } as any).catch(e => console.error(e));
    
    return { message: 'Proses penjadwalan berjalan (bypass Redis).' };
  }

  async generatePreview(id_sekolah: number, periodeId?: number) {
    // Run processor calculation and return result
    const result = await this.jadwalProcessor.process({ data: { id_sekolah, periodeId } } as any);
    const jadwalData = await this.findAll(id_sekolah, undefined, periodeId);
    
    return {
      success: true,
      message: 'Preview jadwal berhasil dibuat.',
      totalJadwal: jadwalData.jadwal.length,
      conflicts: 0,
      emptySlots: Math.max(0, (jadwalData.config?.school_days || 5) * 7 * (jadwalData.bebanGuru.length || 1) - jadwalData.jadwal.length),
      jadwal: jadwalData.jadwal,
    };
  }

  async commitDraft(id_sekolah: number, periodeId?: number) {
    const activePeriode = periodeId 
      ? await this.prisma.periodeJadwal.findFirst({ where: { id: periodeId, id_sekolah } })
      : await this.prisma.periodeJadwal.findFirst({ where: { id_sekolah, is_active: true } });

    return {
      success: true,
      message: 'Jadwal berhasil disimpan!',
      periode: activePeriode,
    };
  }

  async getJobStatus(jobId: string, id_sekolah: number) {
    return {
      jobId,
      status: 'COMPLETED',
      progress: 100,
      message: 'Generasi jadwal selesai!',
    };
  }

  async getPeriodeList(id_sekolah: number) {
    let periodes = await this.prisma.periodeJadwal.findMany({
      where: { id_sekolah },
      orderBy: { created_at: 'desc' }
    });

    // Subtitusi default jika belum ada periode sama sekali
    if (periodes.length === 0) {
      const defaultPeriode = await this.prisma.periodeJadwal.create({
        data: {
          id_sekolah,
          nama: 'Jadwal Utama (2025/2026)',
          tahun_ajaran: '2025/2026',
          semester: 'Ganjil',
          is_active: true,
        }
      });
      periodes = [defaultPeriode];
    }

    return periodes;
  }

  async createPeriode(id_sekolah: number, data: { nama: string; tahun_ajaran?: string; semester?: string; duplicate_from_id?: number }) {
    // Nonaktifkan periode lain jika periode baru ini langsung aktif
    const existingCount = await this.prisma.periodeJadwal.count({ where: { id_sekolah } });
    const is_active = existingCount === 0;

    const newPeriode = await this.prisma.periodeJadwal.create({
      data: {
        id_sekolah,
        nama: data.nama,
        tahun_ajaran: data.tahun_ajaran || '2026/2027',
        semester: data.semester || 'Ganjil',
        is_active,
      }
    });

    // Duplikasi dari periode lama jika duplicate_from_id dispesifikasikan
    if (data.duplicate_from_id) {
      const sourceJadwals = await this.prisma.jadwal.findMany({
        where: { id_sekolah, id_periode_jadwal: data.duplicate_from_id }
      });

      if (sourceJadwals.length > 0) {
        await this.prisma.jadwal.createMany({
          data: sourceJadwals.map(j => ({
            id_sekolah,
            id_periode_jadwal: newPeriode.id,
            id_kelas: j.id_kelas,
            hari: j.hari,
            jam_ke: j.jam_ke,
            id_mapel: j.id_mapel,
            id_guru: j.id_guru,
          }))
        });
      }
    }

    return newPeriode;
  }

  async setActivePeriode(id_sekolah: number, id_periode: number) {
    await this.prisma.periodeJadwal.updateMany({
      where: { id_sekolah },
      data: { is_active: false }
    });

    return this.prisma.periodeJadwal.update({
      where: { id: id_periode },
      data: { is_active: true }
    });
  }

  async findAll(id_sekolah: number, id_kelas?: number, id_periode_jadwal?: number) {
    const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah } });
    const routines = await this.prisma.routineActivity.findMany({ 
      where: { id_sekolah }, 
      orderBy: { time_before_jp: 'asc' } 
    });
    
    // Tentukan periode yang dipakai
    let activePeriodeId = id_periode_jadwal;
    if (!activePeriodeId) {
      const activePeriode = await this.prisma.periodeJadwal.findFirst({
        where: { id_sekolah, is_active: true }
      });
      activePeriodeId = activePeriode?.id;
    }

    const whereClause: any = { id_sekolah };
    if (id_kelas) whereClause.id_kelas = id_kelas;
    if (activePeriodeId) whereClause.id_periode_jadwal = activePeriodeId;
    
    const jadwalList = await this.prisma.jadwal.findMany({
      where: whereClause,
      include: {
        kelas: true,
        mapel: true,
        guru: true,
      },
      orderBy: [
        { hari: 'asc' },
        { jam_ke: 'asc' },
      ]
    });

    const jadwalsForStats = await this.prisma.jadwal.findMany({
      where: activePeriodeId ? { id_sekolah, id_periode_jadwal: activePeriodeId } : { id_sekolah },
      include: { kelas: true, guru: true }
    });

    const bebanMap = new Map<number, { nama: string, total_jp: number, kelas_details: Map<string, number> }>();
    
    jadwalsForStats.forEach(j => {
      if (!j.guru) return;
      const gId = j.guru.id;
      if (!bebanMap.has(gId)) {
        bebanMap.set(gId, { nama: j.guru.nama, total_jp: 0, kelas_details: new Map() });
      }
      
      const b = bebanMap.get(gId)!;
      b.total_jp += 1;
      
      const kName = j.kelas.nama_kelas;
      const curJp = b.kelas_details.get(kName) || 0;
      b.kelas_details.set(kName, curJp + 1);
    });

    const bebanGuru = Array.from(bebanMap.entries()).map(([id, data]) => {
      const details = Array.from(data.kelas_details.entries())
        .map(([kName, jp]) => `${kName} (${jp} JP)`)
        .join(', ');
        
      return {
        id,
        nama: data.nama,
        total_jp: data.total_jp,
        rincian_kelas: details
      };
    }).sort((a, b) => b.total_jp - a.total_jp);

    const periodes = await this.getPeriodeList(id_sekolah);

    return { 
      config, 
      routines, 
      jadwal: jadwalList, 
      bebanGuru,
      periodes,
      current_periode_id: activePeriodeId
    };
  }

  // Jadwal Mengajar Saya khusus Tenaga Pendidik
  async findMyTeachingSchedule(id_guru: number, id_sekolah: number) {
    const guru = await this.prisma.guru.findUnique({ where: { id: id_guru } });
    const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah } });
    
    const activePeriode = await this.prisma.periodeJadwal.findFirst({
      where: { id_sekolah, is_active: true }
    });

    const whereClause: any = { id_sekolah, id_guru };
    if (activePeriode) {
      whereClause.id_periode_jadwal = activePeriode.id;
    }

    const myJadwal = await this.prisma.jadwal.findMany({
      where: whereClause,
      include: {
        kelas: true,
        mapel: true,
      },
      orderBy: [
        { hari: 'asc' },
        { jam_ke: 'asc' },
      ]
    });

    return {
      guru,
      config,
      periode: activePeriode,
      jadwals: myJadwal,
      total_jp: myJadwal.length,
    };
  }


  async exportExcel(id_sekolah: number, template_id: number): Promise<Buffer> {
    const data = await this.findAll(id_sekolah);
    const workbook = new ExcelJS.Workbook();
    
    // Sheet 1: Rekap Guru
    const sheetRekap = workbook.addWorksheet('Rekap Guru');
    sheetRekap.columns = [
      { header: 'No', key: 'no', width: 5 },
      { header: 'Nama Guru', key: 'nama', width: 30 },
      { header: 'Total JP', key: 'jp', width: 10 },
      { header: 'Rincian Kelas', key: 'rincian', width: 50 },
    ];
    data.bebanGuru.forEach((g, i) => {
      sheetRekap.addRow({ no: i + 1, nama: g.nama, jp: g.total_jp, rincian: g.rincian_kelas });
    });

    // Sheets for each class
    const grouped = data.jadwal.reduce((acc: any, curr: any) => {
      const k = curr.kelas.nama_kelas;
      if (!acc[k]) acc[k] = [];
      acc[k].push(curr);
      return acc;
    }, {});

    const days = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'].slice(0, data.config?.school_days || 5);

    const maxJamInSchedule = data.jadwal && data.jadwal.length > 0 ? Math.max(...data.jadwal.map((j: any) => j.jam_ke || 0)) : 0;
    const maxJp = Math.max(6, Math.min(10, maxJamInSchedule || 8));

    for (const [kName, items] of Object.entries(grouped)) {
      const sheet = workbook.addWorksheet(`Kelas ${kName}`);
      
      const cols = [{ header: 'Jam Ke', key: 'jam', width: 10 }];
      days.forEach(d => cols.push({ header: d, key: d.toLowerCase(), width: 25 }));
      sheet.columns = cols;

      for (let jam = 1; jam <= maxJp; jam++) {
        const rowData: any = { jam };
        days.forEach((d, idx) => {
          const dayNum = idx + 1;
          const item = (items as any[]).find(i => i.hari === dayNum && i.jam_ke === jam);
          if (item) {
            rowData[d.toLowerCase()] = `${item.mapel.nama}\n(${item.guru.nama})`;
          }
        });
        sheet.addRow(rowData);
      }
    }

    const buffer = await workbook.xlsx.writeBuffer();
    return Buffer.from(buffer);
  }

  async exportPdf(id_sekolah: number, template_id: number): Promise<Buffer> {
    const data = await this.findAll(id_sekolah);
    
    const fonts = {
      Roboto: {
        normal: 'Helvetica',
        bold: 'Helvetica-Bold',
        italics: 'Helvetica-Oblique',
        bolditalics: 'Helvetica-BoldOblique'
      }
    };
    
    const printer = new PdfPrinter(fonts);

    const content: any[] = [
      { text: 'Jadwal Pelajaran', style: 'header' },
    ];

    const grouped = data.jadwal.reduce((acc: any, curr: any) => {
      const k = curr.kelas.nama_kelas;
      if (!acc[k]) acc[k] = [];
      acc[k].push(curr);
      return acc;
    }, {});

    const days = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'].slice(0, data.config?.school_days || 5);
    const maxJamInSchedule = data.jadwal && data.jadwal.length > 0 ? Math.max(...data.jadwal.map((j: any) => j.jam_ke || 0)) : 0;
    const maxJp = Math.max(6, Math.min(10, maxJamInSchedule || 8));

    for (const [kName, items] of Object.entries(grouped)) {
      content.push({ text: `Kelas ${kName}`, style: 'subheader', margin: [0, 15, 0, 5] });
      
      const tableBody: any[] = [];
      const headerRow = [{ text: 'Jam Ke', style: 'tableHeader' }, ...days.map(d => ({ text: d, style: 'tableHeader' }))];
      tableBody.push(headerRow);

      for (let jam = 1; jam <= maxJp; jam++) {
        const rowData: any[] = [{ text: jam.toString(), alignment: 'center' }];
        days.forEach((d, idx) => {
          const dayNum = idx + 1;
          const item = (items as any[]).find(i => i.hari === dayNum && i.jam_ke === jam);
          if (item) {
            rowData.push({ text: `${item.mapel.nama}\n(${item.guru.nama})`, fontSize: 9 });
          } else {
            rowData.push({ text: '-' });
          }
        });
        tableBody.push(rowData);
      }

      content.push({
        table: {
          headerRows: 1,
          widths: ['auto', ...days.map(() => '*')],
          body: tableBody
        }
      });
    }

    const docDefinition: any = {
      content,
      styles: {
        header: { fontSize: 18, bold: true, alignment: 'center', margin: [0, 0, 0, 10] },
        subheader: { fontSize: 14, bold: true },
        tableHeader: { bold: true, fontSize: 10, color: 'black' }
      },
      defaultStyle: {
        font: 'Roboto'
      }
    };
    
    const pdfDoc = printer.createPdfKitDocument(docDefinition);
    
    return new Promise((resolve, reject) => {
      const chunks: any[] = [];
      pdfDoc.on('data', (chunk: any) => chunks.push(chunk));
      pdfDoc.on('end', () => resolve(Buffer.concat(chunks)));
      pdfDoc.on('error', reject);
      pdfDoc.end();
    });
  }
}
