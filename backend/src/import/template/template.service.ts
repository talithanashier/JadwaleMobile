import { Injectable, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import * as ExcelJS from 'exceljs';

@Injectable()
export class TemplateService {
  constructor(private readonly prisma: PrismaService) {}

  async generateTemplate(entitas: string, sekolahId?: number): Promise<Buffer> {
    const wb = new ExcelJS.Workbook();
    wb.creator = 'Jadwale System';

    let isParallel = false;
    if (sekolahId) {
      const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah: sekolahId } });
      if (config) {
        isParallel = Boolean(config.is_parallel);
      } else {
        const classes = await this.prisma.kelas.findMany({ where: { id_sekolah: sekolahId } });
        if (classes.length > 0) {
          isParallel = classes.some((c) => /[A-Za-z]/.test(c.nama_kelas) && /\d/.test(c.nama_kelas));
        }
      }
    }

    const normalizedEntitas = entitas.toLowerCase().trim();

    switch (normalizedEntitas) {
      case 'guru':
        return this.buildGuruTemplate(wb);
      case 'kelas':
        return this.buildKelasTemplate(wb, isParallel);
      case 'mapel':
        return this.buildMapelTemplate(wb);
      case 'pengampu':
        return this.buildPengampuTemplate(wb, isParallel);
      case 'config':
      case 'pengaturan':
        return this.buildConfigTemplate(wb, isParallel);
      case 'istirahat':
      case 'routine':
        return this.buildIstirahatTemplate(wb);
      case 'jpperhari':
      case 'jp_per_hari':
        return this.buildJpPerHariTemplate(wb, isParallel);
      case 'jadwal':
        return this.buildJadwalTemplate(wb, isParallel);
      case 'siswa':
        return this.buildSiswaTemplate(wb, isParallel);
      case 'all':
        return this.buildAllTemplate(wb, isParallel);
      default:
        throw new BadRequestException(`Template untuk entitas '${entitas}' tidak ditemukan.`);
    }
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────────

  private applyHeaderStyle(row: ExcelJS.Row) {
    row.eachCell((cell) => {
      cell.font = { name: 'Arial', size: 10, bold: true };
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFD9E1F2' } };
      cell.alignment = { horizontal: 'center', vertical: 'middle' };
      cell.border = {
        top: { style: 'thin' },
        left: { style: 'thin' },
        bottom: { style: 'thin' },
        right: { style: 'thin' },
      };
    });
  }

  private addInstructionsSheet(wb: ExcelJS.Workbook, entitasName: string, notes: string[]) {
    let ws = wb.getWorksheet('Petunjuk');
    if (!ws) {
      ws = wb.addWorksheet('Petunjuk');
      ws.getColumn('A').width = 4;
      ws.getColumn('B').width = 90;

      ws.mergeCells('B2:B2');
      ws.getCell('B2').value = 'PETUNJUK PENGISIAN TEMPLATE IMPORT JADWALE (INSTANT GENERATE)';
      ws.getCell('B2').font = { name: 'Arial', size: 12, bold: true };
    }

    let lastRow = Math.max(4, ws.actualRowCount + 2);
    ws.getCell(`B${lastRow}`).value = `--- Petunjuk ${entitasName.toUpperCase()} ---`;
    ws.getCell(`B${lastRow}`).font = { name: 'Arial', size: 10, bold: true };
    lastRow++;

    notes.forEach((note, idx) => {
      ws!.getCell(`B${lastRow}`).value = `${idx + 1}. ${note}`;
      ws!.getCell(`B${lastRow}`).font = { name: 'Arial', size: 10 };
      lastRow++;
    });
  }

  // ─── Sheet builders (no buffer write — used by buildAllTemplate) ──────────────

  private addConfigSheet(wb: ExcelJS.Workbook, isParallel: boolean) {
    const ws = wb.addWorksheet('Pengaturan');
    ws.columns = [
      { header: 'Jumlah Hari Sekolah', key: 'days', width: 22 },
      { header: 'Durasi Per JP (Menit)', key: 'duration', width: 24 },
      { header: 'Sekolah Paralel', key: 'parallel', width: 20 },
      { header: 'Ada Upacara Senin', key: 'ceremony', width: 20 },
    ];
    this.applyHeaderStyle(ws.getRow(1));
    ws.addRow({ days: 5, duration: 35, parallel: isParallel ? 'Ya' : 'Tidak', ceremony: 'Ya' });
    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Pengaturan Sekolah', [
      `Status Profil Sekolah Anda saat ini: ${isParallel ? 'SEKOLAH PARALEL (Rombel A, B, C...)' : 'SEKOLAH NON-PARALEL (Rombel Tunggal I-VI)'}.`,
      'Jumlah Hari Sekolah: Diisi 5 (Senin–Jumat) atau 6 (Senin–Sabtu).',
      'Durasi Per JP (Menit): Diisi misal 35, 40, atau 45.',
      'Sekolah Paralel: Diisi "Ya" jika sekolah memiliki beberapa rombel per tingkat, atau "Tidak" jika hanya 1 rombel per tingkat.',
      'Ada Upacara Senin: Diisi "Ya" atau "Tidak".',
    ]);
  }

  private addIstirahatSheet(wb: ExcelJS.Workbook) {
    const ws = wb.addWorksheet('Istirahat');
    ws.columns = [
      { header: 'Nama Kegiatan', key: 'nama', width: 25 },
      { header: 'Sebelum JP Ke', key: 'sebelumJp', width: 18 },
      { header: 'Durasi (Menit)', key: 'durasi', width: 18 },
    ];
    this.applyHeaderStyle(ws.getRow(1));
    ws.addRow({ nama: 'Istirahat 1', sebelumJp: 4, durasi: 20 });
    ws.addRow({ nama: 'Istirahat 2', sebelumJp: 6, durasi: 20 });
    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Istirahat & Routine', [
      'Nama Kegiatan: Misal Istirahat 1, Istirahat 2, Sholat Dzuhur.',
      'Sebelum JP Ke: Istirahat disisipkan setelah JP ke berapa (misal setelah JP 4).',
      'Durasi (Menit): Durasi kegiatan dalam menit (misal 15, 20).',
    ]);
  }

  private addGuruSheet(wb: ExcelJS.Workbook) {
    const ws = wb.addWorksheet('Guru');
    ws.columns = [
      { header: 'Kode Guru', key: 'kodeGuru', width: 14, style: { numFmt: '@' } },
      { header: 'Nama', key: 'nama', width: 32 },
      { header: 'NIP', key: 'nip', width: 22, style: { numFmt: '@' } },
      { header: 'Jenis Kelamin', key: 'jk', width: 15 },
      { header: 'Status', key: 'status', width: 14 },
      { header: 'Mapel Utama', key: 'mapelUtama', width: 22 },
      { header: 'Max Jam Per Minggu', key: 'maxJam', width: 20 },
      { header: 'Hari Tidak Available', key: 'unavail', width: 24 },
      { header: 'Email', key: 'email', width: 28 },
      { header: 'No HP', key: 'noHp', width: 18, style: { numFmt: '@' } },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    const row = ws.addRow({
      kodeGuru: '02',
      nama: 'Bu Mukti',
      nip: '198705122010012005',
      jk: 'P',
      status: 'PNS',
      mapelUtama: 'PAIBP',
      maxJam: 24,
      unavail: 'JUMAT',
      email: 'mukti@sdnpercobaan.sch.id',
      noHp: '081234567890',
    });

    row.getCell('nip').numFmt = '@';
    row.getCell('noHp').numFmt = '@';
    row.getCell('kodeGuru').numFmt = '@';
    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Guru', [
      'Kode Guru dan Nama WAJIB diisi. Kolom lainnya opsional.',
      'Kode Guru harus unik (misal: 01, 02, atau singkatan nama).',
      'Jenis Kelamin: L atau P.',
      'Status: PNS, PPPK, GTY, atau GTT.',
      'Mapel Utama: pisahkan dengan koma jika mengampu lebih dari satu mapel (misal: MAT,IPA).',
      'Hari Tidak Available: misal "JUMAT" atau "SENIN,RABU".',
      'Kolom NIP dan No HP di-format teks agar angka panjang tidak berubah ke format sains.',
    ]);
  }

  private addKelasSheet(wb: ExcelJS.Workbook, isParallel: boolean) {
    const ws = wb.addWorksheet('Kelas');
    // Kolom "Sekolah Paralel" dan "Fase" dihapus — keduanya auto-derive dari profil sekolah & Tingkat
    ws.columns = [
      { header: 'Nama Kelas', key: 'nama', width: 18 },
      { header: 'Tingkat', key: 'tingkat', width: 12 },
      { header: 'Wali Kelas', key: 'wali', width: 28 },
      { header: 'Jumlah Siswa', key: 'jumlah', width: 15 },
      { header: 'Ruangan', key: 'ruangan', width: 15 },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    if (isParallel) {
      ws.addRow({ nama: 'Kelas 7A', tingkat: 7, wali: 'Pak Budi', jumlah: 32, ruangan: 'R.701' });
      ws.addRow({ nama: 'Kelas 7B', tingkat: 7, wali: 'Bu Siti', jumlah: 30, ruangan: 'R.702' });
      ws.addRow({ nama: 'Kelas 8A', tingkat: 8, wali: 'Pak Joko', jumlah: 32, ruangan: 'R.801' });
      ws.addRow({ nama: 'Kelas 8B', tingkat: 8, wali: 'Bu Anita', jumlah: 31, ruangan: 'R.802' });
    } else {
      ws.addRow({ nama: 'Kelas I', tingkat: 1, wali: 'Bu Mukti', jumlah: 28, ruangan: 'R.101' });
      ws.addRow({ nama: 'Kelas II', tingkat: 2, wali: 'Pak Ahmad', jumlah: 30, ruangan: 'R.102' });
      ws.addRow({ nama: 'Kelas III', tingkat: 3, wali: 'Bu Dewi', jumlah: 29, ruangan: 'R.103' });
      ws.addRow({ nama: 'Kelas IV', tingkat: 4, wali: 'Pak Hendra', jumlah: 31, ruangan: 'R.104' });
      ws.addRow({ nama: 'Kelas V', tingkat: 5, wali: 'Bu Rina', jumlah: 30, ruangan: 'R.105' });
      ws.addRow({ nama: 'Kelas VI', tingkat: 6, wali: 'Pak Doni', jumlah: 29, ruangan: 'R.106' });
    }

    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, `Kelas (${isParallel ? 'Format Paralel' : 'Format Non-Paralel'})`, [
      isParallel
        ? 'Template ini untuk SEKOLAH PARALEL. Nama kelas berakhiran abjad rombel (contoh: 7A, 7B, 8A, X IPA 1).'
        : 'Template ini untuk SEKOLAH NON-PARALEL (Rombel Tunggal). Nama kelas tunggal (contoh: Kelas I, Kelas II).',
      'Nama Kelas dan Tingkat (1–6 untuk SD) WAJIB diisi.',
      'Wali Kelas: isi Nama atau Kode Guru. Kolom ini opsional.',
      'Jumlah Siswa dan Ruangan bersifat opsional.',
    ]);
  }

  private addMapelSheet(wb: ExcelJS.Workbook) {
    const ws = wb.addWorksheet('Mapel');
    // Kolom "Butuh Lab" dan "Fase" dihapus — SD tidak perlu pembedaan fase per mapel
    ws.columns = [
      { header: 'Kode Mapel', key: 'kode', width: 16, style: { numFmt: '@' } },
      { header: 'Nama Mapel', key: 'nama', width: 32 },
      { header: 'Kategori', key: 'kategori', width: 18 },
      { header: 'Jam Per Minggu', key: 'jp', width: 18 },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    const row = ws.addRow({ kode: 'MAT', nama: 'Matematika', kategori: 'WAJIB', jp: 5 });
    row.getCell('kode').numFmt = '@';
    ws.addRow({ kode: 'BIND', nama: 'Bahasa Indonesia', kategori: 'WAJIB', jp: 6 });
    ws.addRow({ kode: 'PAIBP', nama: 'Pendidikan Agama Islam', kategori: 'WAJIB', jp: 3 });

    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Mapel', [
      'Kode Mapel dan Nama Mapel WAJIB diisi. Kode Mapel harus unik.',
      'Kategori: WAJIB, PILIHAN, atau MUATAN_LOKAL.',
      'Jam Per Minggu: Alokasi JP per minggu untuk semua kelas.',
    ]);
  }

  private addPengampuSheet(wb: ExcelJS.Workbook, isParallel: boolean) {
    const ws = wb.addWorksheet('Pengampu');
    ws.columns = [
      { header: 'Kelas', key: 'kelas', width: 18 },
      { header: 'Mapel', key: 'mapel', width: 28 },
      { header: 'Guru', key: 'guru', width: 32 },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    if (isParallel) {
      ws.addRow({ kelas: 'Kelas 7A', mapel: 'Matematika', guru: 'Bu Mukti' });
      ws.addRow({ kelas: 'Kelas 7B', mapel: 'Matematika', guru: 'Pak Hendra' });
    } else {
      ws.addRow({ kelas: 'Kelas I', mapel: 'Matematika', guru: 'Bu Mukti' });
      ws.addRow({ kelas: 'Kelas II', mapel: 'Matematika', guru: 'Pak Ahmad' });
    }

    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Pengampu (Matriks Guru)', [
      'Sheet ini OPSIONAL. Gunakan jika ingin menentukan penugasan guru secara manual untuk tiap Kelas & Mapel.',
      'Jika sheet ini dikosongkan, sistem Jadwale OTOMATIS memetakan guru berdasarkan Mapel Utama & pemerataan jam.',
      'Kolom Kelas, Mapel, dan Guru harus cocok dengan data yang sudah diimpor.',
    ]);
  }

  private addJpPerHariSheet(wb: ExcelJS.Workbook, isParallel: boolean) {
    const ws = wb.addWorksheet('JpPerHari');
    ws.columns = [
      { header: 'Kelas', key: 'kelas', width: 18 },
      { header: 'Hari', key: 'hari', width: 15 },
      { header: 'Jumlah JP', key: 'jp', width: 15 },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    const sampleKelas = isParallel ? 'Kelas 7A' : 'Kelas I';
    ws.addRow({ kelas: sampleKelas, hari: 'SENIN', jp: 7 });
    ws.addRow({ kelas: sampleKelas, hari: 'JUMAT', jp: 5 });
    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'JP Per Hari', [
      'Sheet ini OPSIONAL. Mengatur kuota JP maksimal tiap kelas pada hari tertentu.',
      'Jika dikosongkan, sistem otomatis menggunakan 7 JP per hari untuk semua hari aktif.',
      'Hari diisi: SENIN, SELASA, RABU, KAMIS, JUMAT, atau SABTU.',
    ]);
  }

  private addJadwalSheet(wb: ExcelJS.Workbook, isParallel: boolean) {
    const ws = wb.addWorksheet('Jadwal');
    // Kolom "Catatan" dihapus — tidak diperlukan untuk generate
    ws.columns = [
      { header: 'Hari', key: 'hari', width: 12 },
      { header: 'Jam Ke', key: 'jamKe', width: 10 },
      { header: 'Waktu Mulai', key: 'mulai', width: 14 },
      { header: 'Waktu Selesai', key: 'selesai', width: 14 },
      { header: 'Kelas', key: 'kelas', width: 16 },
      { header: 'Mapel', key: 'mapel', width: 20 },
      { header: 'Guru', key: 'guru', width: 24 },
      { header: 'Tipe', key: 'tipe', width: 16 },
      { header: 'Ruangan', key: 'ruangan', width: 14 },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    ws.addRow({
      hari: 'SENIN',
      jamKe: 1,
      mulai: '07:30',
      selesai: '08:05',
      kelas: isParallel ? 'Kelas 7A' : 'Kelas I',
      mapel: 'MAT',
      guru: 'Bu Mukti',
      tipe: 'PELAJARAN',
      ruangan: isParallel ? 'R.701' : 'R.101',
    });

    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Jadwal Manual', [
      'Sheet ini OPSIONAL. Digunakan hanya jika ingin mengimpor jadwal yang sudah jadi secara manual.',
      'Hari: SENIN, SELASA, RABU, KAMIS, JUMAT, SABTU.',
      'Jam Ke: 1 sampai 10.',
      'Tipe: PELAJARAN, ISTIRAHAT, UPACARA, KULTUM, PEMBIASAAN, atau PROJEK.',
    ]);
  }

  private addSiswaSheet(wb: ExcelJS.Workbook, isParallel: boolean) {
    const ws = wb.addWorksheet('Siswa');
    ws.columns = [
      { header: 'NISN', key: 'nisn', width: 20, style: { numFmt: '@' } },
      { header: 'Nama', key: 'nama', width: 32 },
      { header: 'Kelas', key: 'kelas', width: 16 },
      { header: 'Jenis Kelamin', key: 'jk', width: 15 },
      { header: 'No Telp Orangtua', key: 'telp', width: 22, style: { numFmt: '@' } },
    ];
    this.applyHeaderStyle(ws.getRow(1));

    const row = ws.addRow({
      nisn: '0123456789',
      nama: 'Ahmad Fauzi',
      kelas: isParallel ? 'Kelas 7A' : 'Kelas I',
      jk: 'L',
      telp: '081299887766',
    });
    row.getCell('nisn').numFmt = '@';
    row.getCell('telp').numFmt = '@';
    ws.views = [{ state: 'frozen', ySplit: 1 }];

    this.addInstructionsSheet(wb, 'Siswa', [
      'NISN, Nama, dan Kelas WAJIB diisi.',
      'Kolom NISN dan No Telp di-format teks agar tidak berubah ke format sains.',
      'Jenis Kelamin: L atau P.',
    ]);
  }

  // ─── Public template builders (write buffer) ──────────────────────────────────

  private async buildConfigTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addConfigSheet(wb, isParallel);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildIstirahatTemplate(wb: ExcelJS.Workbook): Promise<Buffer> {
    this.addIstirahatSheet(wb);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildGuruTemplate(wb: ExcelJS.Workbook): Promise<Buffer> {
    this.addGuruSheet(wb);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildKelasTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addKelasSheet(wb, isParallel);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildMapelTemplate(wb: ExcelJS.Workbook): Promise<Buffer> {
    this.addMapelSheet(wb);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildPengampuTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addPengampuSheet(wb, isParallel);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildJpPerHariTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addJpPerHariSheet(wb, isParallel);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildJadwalTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addJadwalSheet(wb, isParallel);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  private async buildSiswaTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addSiswaSheet(wb, isParallel);
    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }

  /**
   * Builds a single workbook with ALL sheets — used for the "Download All" template.
   * Sheet order follows the recommended import order for Instant Generate.
   */
  private async buildAllTemplate(wb: ExcelJS.Workbook, isParallel: boolean): Promise<Buffer> {
    this.addConfigSheet(wb, isParallel);
    this.addIstirahatSheet(wb);
    this.addGuruSheet(wb);
    this.addKelasSheet(wb, isParallel);
    this.addMapelSheet(wb);
    this.addPengampuSheet(wb, isParallel);
    this.addJpPerHariSheet(wb, isParallel);
    this.addSiswaSheet(wb, isParallel);
    this.addJadwalSheet(wb, isParallel);

    const buf = await wb.xlsx.writeBuffer();
    return Buffer.from(buf);
  }
}
