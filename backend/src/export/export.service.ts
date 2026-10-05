import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import * as ExcelJS from 'exceljs';
import pdfMake from './pdfmake.config';

const DAYS = [
  { id: 1, name: 'SENIN' },
  { id: 2, name: 'SELASA' },
  { id: 3, name: 'RABU' },
  { id: 4, name: 'KAMIS' },
  { id: 5, name: 'JUMAT' },
  { id: 6, name: 'SABTU' },
];

@Injectable()
export class ExportService {
  constructor(private readonly prisma: PrismaService) {}

  private getMapelCode(nama: string): string {
    const MAP: Record<string, string> = {
      'pendidikan agama dan budi pekerti': 'PAIBP',
      'pendidikan agama islam': 'PAIBP',
      'paibp': 'PAIBP',
      'pendidikan pancasila': 'PP',
      'bahasa indonesia': 'B.IND',
      'matematika': 'MAT',
      'ilmu pengetahuan alam dan sosial': 'IPAS',
      'ipas': 'IPAS',
      'seni dan budaya': 'SB',
      'sbdp': 'SB',
      'pendidikan jasmani olahraga dan kesehatan': 'PJOK',
      'pjok': 'PJOK',
      'olahraga': 'PJOK',
      'bahasa inggris': 'B.ING',
      'bahasa jawa': 'BJW',
      'btq': 'BTQ',
      'baca tulis quran': 'BTQ',
    };
    const lower = nama.toLowerCase();
    if (MAP[lower]) return MAP[lower];
    if (nama.length <= 6) return nama.toUpperCase();
    return nama.substring(0, 5).toUpperCase();
  }

  private getFaseLabel(kelas: any): string {
    if (kelas.fase) return kelas.fase;
    const namaTingkat = kelas.tingkatan?.nama || '';
    const numMatch = namaTingkat.match(/\d+/);
    const t = numMatch ? parseInt(numMatch[0], 10) : 1;
    if (t <= 2) return 'A';
    if (t <= 4) return 'B';
    if (t <= 6) return 'C';
    if (t <= 9) return 'D';
    if (t === 10) return 'E';
    return 'F';
  }

  private async fetchJadwalData(id_sekolah: number, periodeId?: number) {
    const sekolah = await this.prisma.sekolah.findUnique({
      where: { id: id_sekolah },
      include: { config: true },
    });

    if (!sekolah) {
      throw new NotFoundException('Sekolah tidak ditemukan');
    }

    let periode = null;
    if (periodeId) {
      periode = await this.prisma.periodeJadwal.findFirst({
        where: { id: periodeId, id_sekolah },
      });
    } else {
      periode = await this.prisma.periodeJadwal.findFirst({
        where: { id_sekolah, is_active: true },
      });
    }

    const whereClause: any = { id_sekolah };
    if (periode?.id) {
      whereClause.id_periode_jadwal = periode.id;
    }

    const jadwals = await this.prisma.jadwal.findMany({
      where: whereClause,
      include: {
        kelas: { include: { tingkatan: true } },
        mapel: true,
        guru: true,
      },
      orderBy: [{ hari: 'asc' }, { jam_ke: 'asc' }],
    });

    const kelases = await this.prisma.kelas.findMany({
      where: { id_sekolah },
      include: { tingkatan: true },
      orderBy: [{ id_tingkatan: 'asc' }, { nama_kelas: 'asc' }],
    });

    const gurus = await this.prisma.guru.findMany({
      where: { id_sekolah },
      orderBy: { nama: 'asc' },
    });

    return { sekolah, periode, jadwals, kelases, gurus };
  }

  async generateExcel(id_sekolah: number, periodeId?: number): Promise<Buffer> {
    const { sekolah, periode, jadwals, kelases, gurus } = await this.fetchJadwalData(id_sekolah, periodeId);
    const workbook = new ExcelJS.Workbook();
    workbook.creator = 'Jadwale System';
    workbook.created = new Date();

    const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah } });
    const routines = await this.prisma.routineActivity.findMany({
      where: { id_sekolah },
      orderBy: { time_before_jp: 'asc' },
    });

    const schoolDays = config?.school_days || 5;
    const durationJp = config?.duration_per_jp || 35;
    const startTimeRaw = config?.start_time || new Date('1970-01-01T07:00:00Z');
    const hasCeremony = config?.has_monday_ceremony ?? true;

    // Helper: convert 1-based column index to Excel letter (1->A, 26->Z, 27->AA)
    const getColLetter = (colIdx: number): string => {
      let temp = colIdx;
      let letter = '';
      while (temp > 0) {
        const m = (temp - 1) % 26;
        letter = String.fromCharCode(65 + m) + letter;
        temp = Math.floor((temp - m) / 26);
      }
      return letter;
    };

    const targetClasses = kelases; // All classes without truncation!
    const totalClasses = targetClasses.length;
    const startClassColIdx = 5; // Col E (1=A, 2=B, 3=C, 4=D, 5=E)
    const lastClassColIdx = startClassColIdx + totalClasses - 1;
    const lastClassColLetter = getColLetter(lastClassColIdx);

    // ─── Build dynamic time slots ──────────────────────────────────────────────
    interface DynSlot {
      label: string;
      jamKe: number | null;
      tipe: 'PELAJARAN' | 'ISTIRAHAT' | 'ROUTINE';
      name?: string;
    }

    const buildTimeSlots = (): DynSlot[] => {
      const slots: DynSlot[] = [];
      let cursor = new Date(startTimeRaw);
      const addMins = (d: Date, m: number): Date => {
        const nd = new Date(d);
        nd.setUTCMinutes(nd.getUTCMinutes() + m);
        return nd;
      };
      const fmt = (d: Date): string => {
        const h = String(d.getUTCHours()).padStart(2, '0');
        const m = String(d.getUTCMinutes()).padStart(2, '0');
        return `${h}:${m}`;
      };

      // Pre-school routine (Pembiasaan/Upacara)
      const preRoutine = routines.find(r => r.time_before_jp === 1);
      const preMin = preRoutine ? preRoutine.duration : 15;
      const preEnd = addMins(cursor, preMin);
      slots.push({ label: `${fmt(cursor)} - ${fmt(preEnd)}`, jamKe: null, tipe: 'ROUTINE', name: preRoutine?.name || 'Kegiatan Pembiasaan Pagi / Upacara' });
      cursor = preEnd;

      const maxJamInSchedule = jadwals && jadwals.length > 0 ? Math.max(...jadwals.map(j => j.jam_ke || 0)) : 0;
      const maxJp = Math.max(6, Math.min(10, maxJamInSchedule || 7));

      for (let jp = 1; jp <= maxJp; jp++) {
        const end = addMins(cursor, durationJp);
        slots.push({ label: `${fmt(cursor)} - ${fmt(end)}`, jamKe: jp, tipe: 'PELAJARAN' });
        cursor = end;
        const breakAfter = routines.find(r => r.time_before_jp === jp + 1);
        if (breakAfter) {
          const breakEnd = addMins(cursor, breakAfter.duration);
          slots.push({ label: `${fmt(cursor)} - ${fmt(breakEnd)}`, jamKe: null, tipe: 'ISTIRAHAT', name: breakAfter.name || 'Istirahat' });
          cursor = breakEnd;
        }
      }
      return slots;
    };

    const timeSlots = buildTimeSlots();
    const activeDays = DAYS.slice(0, schoolDays);

    // Schedule map: key = `${hari}_${jam_ke}_${id_kelas}`
    const scheduleMap = new Map<string, { mapel: string; guru: string; color?: string }>();
    jadwals.forEach((j) => {
      scheduleMap.set(`${j.hari}_${j.jam_ke}_${j.id_kelas}`, {
        mapel: j.mapel.nama,
        guru: j.guru.nama,
        color: j.mapel.color,
      });
    });

    const borderStyle = { top: { style: 'thin' as const }, left: { style: 'thin' as const }, bottom: { style: 'thin' as const }, right: { style: 'thin' as const } };

    const hStyle = (cell: ExcelJS.Cell, fgArgb = 'FFD9E1F2') => {
      cell.font = { name: 'Arial', size: 10, bold: true };
      cell.alignment = { horizontal: 'center', vertical: 'middle' };
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: fgArgb } };
      cell.border = borderStyle;
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // SHEET 1: Jadwal Pelajaran (Matriks Semua Kelas)
    // ═══════════════════════════════════════════════════════════════════════════
    const ws = workbook.addWorksheet('Jadwal Pelajaran');

    ws.getColumn('A').width = 3;
    ws.getColumn('B').width = 12;
    ws.getColumn('C').width = 16;
    ws.getColumn('D').width = 10;
    targetClasses.forEach((_, idx) => {
      ws.getColumn(getColLetter(startClassColIdx + idx)).width = 22;
    });

    // Title Block
    ws.mergeCells(`C1:${lastClassColLetter}1`);
    const titleCell = ws.getCell('C1');
    titleCell.value = 'JADWAL PELAJARAN';
    titleCell.font = { name: 'Arial', size: 16, bold: true };
    titleCell.alignment = { horizontal: 'center', vertical: 'middle' };
    ws.getRow(1).height = 28;

    ws.mergeCells(`C2:${lastClassColLetter}2`);
    const subCell = ws.getCell('C2');
    subCell.value = sekolah.nama_sekolah.toUpperCase();
    subCell.font = { name: 'Arial', size: 13, bold: true };
    subCell.alignment = { horizontal: 'center', vertical: 'middle' };

    ws.mergeCells(`C3:${lastClassColLetter}3`);
    const metaCell = ws.getCell('C3');
    metaCell.value = `TAHUN AJARAN ${periode?.tahun_ajaran || '2026/2027'} — SEMESTER ${periode?.semester?.toUpperCase() || 'GANJIL'}`;
    metaCell.font = { name: 'Arial', size: 11, bold: true };
    metaCell.alignment = { horizontal: 'center', vertical: 'middle' };

    // Headers (Rows 5-7)
    ws.getRow(5).height = 22;
    ws.getRow(6).height = 18;
    ws.getRow(7).height = 20;

    ws.mergeCells('B5:B7'); ws.getCell('B5').value = 'HARI';
    ws.mergeCells('C5:C7'); ws.getCell('C5').value = 'WAKTU';
    ws.mergeCells('D5:D7'); ws.getCell('D5').value = 'JAM KE';
    ws.mergeCells(`E5:${lastClassColLetter}5`); ws.getCell('E5').value = 'KELAS';

    // Style B5:D7 & E5
    ['B', 'C', 'D'].forEach(c => ['5', '6', '7'].forEach(r => hStyle(ws.getCell(`${c}${r}`))));
    hStyle(ws.getCell('E5'));

    // Row 6: Fase labels
    targetClasses.forEach((k, idx) => {
      const colLetter = getColLetter(startClassColIdx + idx);
      const cell = ws.getCell(`${colLetter}6`);
      cell.value = `FASE ${this.getFaseLabel(k)}`;
      hStyle(cell, 'FFE2EFDA');
    });

    // Row 7: Class Full Display Name (e.g. 1A, 1B, 2A, 2B, ..., 6B)
    targetClasses.forEach((k, idx) => {
      const colLetter = getColLetter(startClassColIdx + idx);
      const cell = ws.getCell(`${colLetter}7`);
      cell.value = `Kelas ${k.kode_lengkap || k.nama_kelas}`;
      hStyle(cell, 'FFE2EFDA');
    });

    // Render Data Rows
    let currentRow = 8;
    activeDays.forEach((day) => {
      const startRowForDay = currentRow;

      timeSlots.forEach((slot) => {
        ws.getRow(currentRow).height = slot.tipe === 'PELAJARAN' ? 32 : 22;

        if (slot.tipe === 'ISTIRAHAT') {
          ws.mergeCells(`C${currentRow}:${lastClassColLetter}${currentRow}`);
          const cell = ws.getCell(`C${currentRow}`);
          cell.value = `${slot.label}  —  ${slot.name || 'ISTIRAHAT'}`;
          cell.font = { name: 'Arial', size: 9, bold: true };
          cell.alignment = { horizontal: 'center', vertical: 'middle' };
          for (let cIdx = 2; cIdx <= lastClassColIdx; cIdx++) {
            const cc = ws.getCell(`${getColLetter(cIdx)}${currentRow}`);
            cc.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFF2CC' } };
            cc.border = borderStyle;
          }

        } else if (slot.tipe === 'ROUTINE') {
          const waktuCell = ws.getCell(`C${currentRow}`);
          waktuCell.value = slot.label;
          waktuCell.font = { name: 'Arial', size: 9 };
          waktuCell.alignment = { horizontal: 'center', vertical: 'middle' };
          waktuCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF2F2F2' } };
          waktuCell.border = borderStyle;

          ws.mergeCells(`D${currentRow}:${lastClassColLetter}${currentRow}`);
          const routineLabel = day.id === 1 && hasCeremony ? 'UPACARA BENDERA' : day.id === 5 ? 'KULTUM / PEMBIASAAN JUMAT' : (slot.name || 'KEGIATAN PEMBIASAAN PAGI');
          const routineCell = ws.getCell(`D${currentRow}`);
          routineCell.value = routineLabel;
          routineCell.font = { name: 'Arial', size: 9, bold: true };
          routineCell.alignment = { horizontal: 'center', vertical: 'middle' };
          for (let cIdx = 4; cIdx <= lastClassColIdx; cIdx++) {
            const cc = ws.getCell(`${getColLetter(cIdx)}${currentRow}`);
            cc.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFCE4D6' } };
            cc.border = borderStyle;
          }

        } else {
          // PELAJARAN
          const waktuCell = ws.getCell(`C${currentRow}`);
          waktuCell.value = slot.label;
          waktuCell.font = { name: 'Arial', size: 9 };
          waktuCell.alignment = { horizontal: 'center', vertical: 'middle' };
          waktuCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF2F2F2' } };
          waktuCell.border = borderStyle;

          const jamCell = ws.getCell(`D${currentRow}`);
          jamCell.value = slot.jamKe;
          jamCell.font = { name: 'Arial', size: 9, bold: true };
          jamCell.alignment = { horizontal: 'center', vertical: 'middle' };
          jamCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF2F2F2' } };
          jamCell.border = borderStyle;

          targetClasses.forEach((k, idx) => {
            const colLetter = getColLetter(startClassColIdx + idx);
            const cell = ws.getCell(`${colLetter}${currentRow}`);
            const item = scheduleMap.get(`${day.id}_${slot.jamKe}_${k.id}`);
            if (item) {
              cell.value = `${item.mapel}\n(${item.guru})`;
              cell.font = { name: 'Arial', size: 9 };
              cell.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
            } else {
              cell.value = '-';
              cell.font = { name: 'Arial', size: 9, color: { argb: 'FF999999' } };
              cell.alignment = { horizontal: 'center', vertical: 'middle' };
            }
            cell.border = borderStyle;
          });
        }

        currentRow++;
      });

      // Merge HARI column vertically
      ws.mergeCells(`B${startRowForDay}:B${currentRow - 1}`);
      const dayCell = ws.getCell(`B${startRowForDay}`);
      dayCell.value = day.name;
      dayCell.font = { name: 'Arial', size: 10, bold: true };
      dayCell.alignment = { horizontal: 'center', vertical: 'middle', textRotation: 90 };
      for (let r = startRowForDay; r < currentRow; r++) {
        const c = ws.getCell(`B${r}`);
        c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE2EFDA' } };
        c.border = borderStyle;
      }
    });

    // ═══════════════════════════════════════════════════════════════════════════
    // SHEET 2: Rekap Beban Mengajar Guru
    // ═══════════════════════════════════════════════════════════════════════════
    const ws2 = workbook.addWorksheet('Rekap Beban Mengajar Guru');
    ws2.getColumn('A').width = 6;
    ws2.getColumn('B').width = 32;
    ws2.getColumn('C').width = 22;
    ws2.getColumn('D').width = 12;
    ws2.getColumn('E').width = 50;

    ws2.mergeCells('A1:E1');
    ws2.getCell('A1').value = `REKAP BEBAN MENGAJAR GURU - ${sekolah.nama_sekolah.toUpperCase()}`;
    ws2.getCell('A1').font = { name: 'Arial', size: 14, bold: true };
    ws2.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };
    ws2.getRow(1).height = 28;

    ws2.mergeCells('A2:E2');
    ws2.getCell('A2').value = `TAHUN AJARAN ${periode?.tahun_ajaran || '2026/2027'} — SEMESTER ${periode?.semester?.toUpperCase() || 'GANJIL'}`;
    ws2.getCell('A2').font = { name: 'Arial', size: 10, bold: true };
    ws2.getCell('A2').alignment = { horizontal: 'center', vertical: 'middle' };

    ws2.getRow(4).height = 24;
    ['No', 'Nama Guru', 'NIP', 'Total JP', 'Rincian Mengajar Per Kelas'].forEach((h, idx) => {
      const colLetter = getColLetter(idx + 1);
      const cell = ws2.getCell(`${colLetter}4`);
      cell.value = h;
      hStyle(cell, 'FFD9E1F2');
    });

    // Calculate teacher loads
    const bebanMap = new Map<number, { nama: string; nip: string; total_jp: number; kelasMap: Map<string, number> }>();
    gurus.forEach(g => {
      bebanMap.set(g.id, { nama: g.nama, nip: g.nip || '-', total_jp: 0, kelasMap: new Map() });
    });

    jadwals.forEach(j => {
      if (!j.guru) return;
      const b = bebanMap.get(j.guru.id);
      if (b) {
        b.total_jp += 1;
        const kName = j.kelas?.kode_lengkap || j.kelas?.nama_kelas || 'Kelas';
        b.kelasMap.set(kName, (b.kelasMap.get(kName) || 0) + 1);
      }
    });

    const guruBebanList = Array.from(bebanMap.values()).sort((a, b) => b.total_jp - a.total_jp);

    guruBebanList.forEach((g, i) => {
      const r = 5 + i;
      ws2.getRow(r).height = 20;

      const rincian = Array.from(g.kelasMap.entries())
        .map(([k, jp]) => `${k} (${jp} JP)`)
        .join(', ');

      ws2.getCell(`A${r}`).value = i + 1;
      ws2.getCell(`B${r}`).value = g.nama;
      ws2.getCell(`C${r}`).value = g.nip;
      ws2.getCell(`D${r}`).value = g.total_jp;
      ws2.getCell(`E${r}`).value = rincian || '-';

      ws2.getCell(`A${r}`).alignment = { horizontal: 'center', vertical: 'middle' };
      ws2.getCell(`B${r}`).alignment = { horizontal: 'left', vertical: 'middle' };
      ws2.getCell(`C${r}`).alignment = { horizontal: 'center', vertical: 'middle' };
      ws2.getCell(`D${r}`).alignment = { horizontal: 'center', vertical: 'middle' };
      ws2.getCell(`E${r}`).alignment = { horizontal: 'left', vertical: 'middle' };

      ['A', 'B', 'C', 'D', 'E'].forEach(c => {
        const cell = ws2.getCell(`${c}${r}`);
        cell.font = { name: 'Arial', size: 9, bold: c === 'D' };
        cell.border = borderStyle;
      });
    });

    // Total Row
    const lastGRow = 5 + guruBebanList.length;
    ws2.getCell(`A${lastGRow}`).value = 'TOTAL ALOKASI JAM MENGAJAR';
    ws2.mergeCells(`A${lastGRow}:C${lastGRow}`);
    const sumJp = guruBebanList.reduce((acc, curr) => acc + curr.total_jp, 0);
    ws2.getCell(`D${lastGRow}`).value = sumJp;
    ws2.getCell(`E${lastGRow}`).value = `${jadwals.length} Jam Pelajaran Terjadwal`;

    ['A', 'B', 'C', 'D', 'E'].forEach(c => {
      const cell = ws2.getCell(`${c}${lastGRow}`);
      cell.font = { name: 'Arial', size: 10, bold: true };
      cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF2F2F2' } };
      cell.alignment = { horizontal: c === 'D' ? 'center' : 'left', vertical: 'middle' };
      cell.border = borderStyle;
    });

    // ═══════════════════════════════════════════════════════════════════════════
    // SHEET 3: Alokasi JP Per Mapel & Kelas
    // ═══════════════════════════════════════════════════════════════════════════
    const ws3 = workbook.addWorksheet('Alokasi JP Per Mapel');
    const ws3LastColLetter = getColLetter(3 + totalClasses);

    ws3.getColumn('A').width = 6;
    ws3.getColumn('B').width = 35;
    ws3.getColumn('C').width = 12;
    targetClasses.forEach((_, idx) => {
      ws3.getColumn(getColLetter(4 + idx)).width = 10;
    });
    ws3.getColumn(ws3LastColLetter).width = 12;

    ws3.mergeCells(`A1:${ws3LastColLetter}1`);
    ws3.getCell('A1').value = `TABEL ALOKASI WAKTU / JP MATERI - ${sekolah.nama_sekolah.toUpperCase()}`;
    ws3.getCell('A1').font = { name: 'Arial', size: 14, bold: true };
    ws3.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };
    ws3.getRow(1).height = 28;

    // Row 4: headers
    ws3.getRow(4).height = 22;
    ws3.getCell('A4').value = 'No'; hStyle(ws3.getCell('A4'));
    ws3.getCell('B4').value = 'MATA PELAJARAN'; hStyle(ws3.getCell('B4'));
    ws3.getCell('C4').value = 'KODE'; hStyle(ws3.getCell('C4'));

    targetClasses.forEach((k, idx) => {
      const col = getColLetter(4 + idx);
      const cell = ws3.getCell(`${col}4`);
      cell.value = k.kode_lengkap || k.nama_kelas;
      hStyle(cell, 'FFE2EFDA');
    });

    ws3.getCell(`${ws3LastColLetter}4`).value = 'TOTAL JP';
    hStyle(ws3.getCell(`${ws3LastColLetter}4`));

    // Get unique mapels from DB
    const mapelsDB = await this.prisma.mapel.findMany({ where: { id_sekolah } });
    mapelsDB.forEach((m, idx) => {
      const r = 5 + idx;
      ws3.getRow(r).height = 20;
      const kode = this.getMapelCode(m.nama);

      ws3.getCell(`A${r}`).value = idx + 1;
      ws3.getCell(`B${r}`).value = m.nama;
      ws3.getCell(`C${r}`).value = kode;

      ws3.getCell(`A${r}`).alignment = { horizontal: 'center', vertical: 'middle' };
      ws3.getCell(`B${r}`).alignment = { horizontal: 'left', vertical: 'middle' };
      ws3.getCell(`C${r}`).alignment = { horizontal: 'center', vertical: 'middle' };

      let totalMapelJp = 0;
      targetClasses.forEach((k, kIdx) => {
        const colLetter = getColLetter(4 + kIdx);
        const count = jadwals.filter(j => j.id_kelas === k.id && j.id_mapel === m.id).length;
        const cell = ws3.getCell(`${colLetter}${r}`);
        cell.value = count || 0;
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
        totalMapelJp += count;
      });

      const totalCell = ws3.getCell(`${ws3LastColLetter}${r}`);
      totalCell.value = totalMapelJp;
      totalCell.alignment = { horizontal: 'center', vertical: 'middle' };

      for (let cIdx = 1; cIdx <= 3 + totalClasses; cIdx++) {
        const cell = ws3.getCell(`${getColLetter(cIdx)}${r}`);
        cell.font = { name: 'Arial', size: 9, bold: cIdx === 3 + totalClasses };
        cell.border = borderStyle;
      }
    });

    const buffer = await workbook.xlsx.writeBuffer();
    return Buffer.from(buffer);
  }

  async generatePdf(id_sekolah: number, periodeId?: number): Promise<Buffer> {
    const { sekolah, periode, jadwals, kelases } = await this.fetchJadwalData(id_sekolah, periodeId);
    const targetClasses = kelases.slice(0, 6);

    const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah } });
    const routines = await this.prisma.routineActivity.findMany({
      where: { id_sekolah },
      orderBy: { time_before_jp: 'asc' },
    });

    const schoolDays = config?.school_days || 5;
    const durationJp = config?.duration_per_jp || 35;
    const startTimeRaw = config?.start_time || new Date('1970-01-01T07:00:00Z');
    const hasCeremony = config?.has_monday_ceremony ?? true;

    interface DynSlot {
      label: string;
      jamKe: number | null;
      tipe: 'PELAJARAN' | 'ISTIRAHAT' | 'ROUTINE';
    }

    const addMins = (d: Date, m: number): Date => {
      const nd = new Date(d);
      nd.setUTCMinutes(nd.getUTCMinutes() + m);
      return nd;
    };
    const fmt = (d: Date): string => {
      const h = String(d.getUTCHours()).padStart(2, '0');
      const m = String(d.getUTCMinutes()).padStart(2, '0');
      return `${h}:${m}`;
    };

    const buildTimeSlots = (): DynSlot[] => {
      const slots: DynSlot[] = [];
      let cursor = new Date(startTimeRaw);
      const preRoutine = routines.find(r => r.time_before_jp === 1);
      const preEnd = addMins(cursor, preRoutine ? preRoutine.duration : 30);
      slots.push({ label: `${fmt(cursor)} - ${fmt(preEnd)}`, jamKe: null, tipe: 'ROUTINE' });
      cursor = preEnd;
      for (let jp = 1; jp <= 7; jp++) {
        const end = addMins(cursor, durationJp);
        slots.push({ label: `${fmt(cursor)} - ${fmt(end)}`, jamKe: jp, tipe: 'PELAJARAN' });
        cursor = end;
        const breakAfter = routines.find(r => r.time_before_jp === jp + 1);
        if (breakAfter) {
          const breakEnd = addMins(cursor, breakAfter.duration);
          slots.push({ label: `${fmt(cursor)} - ${fmt(breakEnd)}`, jamKe: null, tipe: 'ISTIRAHAT' });
          cursor = breakEnd;
        }
      }
      return slots;
    };

    const timeSlots = buildTimeSlots();
    const activeDays = DAYS.slice(0, schoolDays);

    const scheduleMap = new Map<string, string>();
    jadwals.forEach((j) => {
      scheduleMap.set(`${j.hari}_${j.jam_ke}_${j.id_kelas}`, this.getMapelCode(j.mapel.nama));
    });

    const nCols = targetClasses.length || 6;

    const tableBody: any[] = [
      [
        { text: 'HARI', rowSpan: 3, style: 'th' },
        { text: 'WAKTU', rowSpan: 3, style: 'th' },
        { text: 'JAM KE', rowSpan: 3, style: 'th' },
        { text: 'KELAS', colSpan: nCols, style: 'th' },
        ...Array(Math.max(0, nCols - 1)).fill({}),
      ],
      [
        {}, {}, {},
        ...targetClasses.map(k => ({ text: `FASE ${this.getFaseLabel(k)}`, style: 'thFase' })),
      ],
      [
        {}, {}, {},
        ...targetClasses.map(k => ({ text: k.nama_kelas, style: 'thSub' })),
      ],
    ];

    activeDays.forEach((day) => {
      timeSlots.forEach((slot, sIdx) => {
        const isFirst = sIdx === 0;
        const dayCell = isFirst
          ? { text: day.name, rowSpan: timeSlots.length, style: 'dayCell' }
          : {};

        if (slot.tipe === 'ISTIRAHAT') {
          tableBody.push([
            dayCell,
            { text: `${slot.label} — ISTIRAHAT`, colSpan: nCols + 2, style: 'breakCell' },
            ...Array(nCols + 1).fill({}),
          ]);
        } else if (slot.tipe === 'ROUTINE') {
          const routineLabel = day.id === 1 && hasCeremony ? 'UPACARA' : day.id === 5 ? 'KULTUM' : 'PEMBIASAAN';
          tableBody.push([
            dayCell,
            { text: slot.label, style: 'timeCell' },
            { text: routineLabel, colSpan: nCols + 1, style: 'routineCell' },
            ...Array(nCols).fill({}),
          ]);
        } else {
          const classCells = targetClasses.map((k) => {
            const val = scheduleMap.get(`${day.id}_${slot.jamKe}_${k.id}`) || '';
            return { text: val, style: 'cellText' };
          });
          tableBody.push([
            dayCell,
            { text: slot.label, style: 'timeCell' },
            { text: slot.jamKe?.toString() || '', style: 'jamCell' },
            ...classCells,
          ]);
        }
      });
    });

    const docDefinition: any = {
      pageSize: 'A4',
      pageOrientation: 'landscape',
      pageMargins: [20, 30, 20, 30],
      content: [
        { text: 'JADWAL PELAJARAN', style: 'docTitle' },
        { text: sekolah.nama_sekolah.toUpperCase(), style: 'docSubtitle' },
        {
          text: `TAHUN AJARAN ${periode?.tahun_ajaran || '2026/2027'}`,
          style: 'docMeta',
          margin: [0, 0, 0, 12],
        },
        {
          table: {
            headerRows: 3,
            widths: [45, 65, 30, ...Array(nCols).fill('*')],
            body: tableBody,
          },
          layout: {
            hLineWidth: () => 0.5,
            vLineWidth: () => 0.5,
            hLineColor: () => '#A0A0A0',
            vLineColor: () => '#A0A0A0',
          },
        },
      ],
      footer: (currentPage: number, pageCount: number) => ({
        text: `Dicetak: ${new Date().toLocaleDateString('id-ID')} | Halaman ${currentPage} dari ${pageCount}`,
        alignment: 'right',
        fontSize: 8,
        margin: [0, 0, 20, 0],
        color: '#666666',
      }),
      styles: {
        docTitle: { fontSize: 14, bold: true, alignment: 'center' },
        docSubtitle: { fontSize: 11, bold: true, alignment: 'center' },
        docMeta: { fontSize: 9, alignment: 'center' },
        th: { fontSize: 8, bold: true, alignment: 'center', fillColor: '#D9E1F2' },
        thFase: { fontSize: 7, bold: true, alignment: 'center', fillColor: '#E2EFDA' },
        thSub: { fontSize: 8, bold: true, alignment: 'center', fillColor: '#E2EFDA' },
        dayCell: { fontSize: 8, bold: true, alignment: 'center', fillColor: '#E2EFDA' },
        timeCell: { fontSize: 7, alignment: 'center', fillColor: '#F2F2F2' },
        jamCell: { fontSize: 7, bold: true, alignment: 'center', fillColor: '#F2F2F2' },
        breakCell: { fontSize: 7, bold: true, alignment: 'center', fillColor: '#FFF2CC' },
        routineCell: { fontSize: 7, bold: true, alignment: 'center', fillColor: '#FCE4D6' },
        cellText: { fontSize: 8, bold: true, alignment: 'center' },
      },
      defaultStyle: { fontSize: 8 },
    };

    const pdfmake = require('pdfmake');
    const fonts = {
      Roboto: {
        normal: 'Helvetica',
        bold: 'Helvetica-Bold',
        italics: 'Helvetica-Oblique',
        bolditalics: 'Helvetica-BoldOblique'
      }
    };
    pdfmake.setFonts(fonts);

    const doc = pdfmake.createPdf(docDefinition);
    return await doc.getBuffer();
  }
}
