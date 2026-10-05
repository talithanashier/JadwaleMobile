import { Injectable, BadRequestException, InternalServerErrorException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { parseExcel } from './utils/excel.util';
import { parseCsv } from './utils/csv.util';
import { validateRow } from './utils/validation.util';
import { GURU_SCHEMA } from './schemas/guru.schema';
import { KELAS_SCHEMA } from './schemas/kelas.schema';
import { MAPEL_SCHEMA } from './schemas/mapel.schema';
import { JADWAL_SCHEMA } from './schemas/jadwal.schema';
import { SISWA_SCHEMA } from './schemas/siswa.schema';
import { CONFIG_SCHEMA } from './schemas/config.schema';
import { PENGAMPU_SCHEMA } from './schemas/pengampu.schema';
import * as crypto from 'crypto';

export class UploadedFileType {
  fieldname?: string;
  originalname!: string;
  encoding?: string;
  mimetype?: string;
  size?: number;
  buffer!: Buffer;
}

export interface PreviewRowResult {
  rowNumber: number;
  status: 'VALID' | 'INVALID' | 'DUPLICATE';
  data: Record<string, any>;
  errors: string[];
}

export interface PreviewResponse {
  totalRows: number;
  validRows: number;
  invalidRows: number;
  duplicateRows: number;
  previewToken: string;
  rows: PreviewRowResult[];
  entitas: string;
  sekolahId: number;
  mode: string;
}

interface CacheItem {
  token: string;
  sekolahId: number;
  entitas: string;
  mode: string;
  rows: PreviewRowResult[];
  createdAt: number;
}

@Injectable()
export class ImportService {
  private cache = new Map<string, CacheItem>();

  constructor(private readonly prisma: PrismaService) {
    setInterval(() => {
      const now = Date.now();
      for (const [key, item] of this.cache.entries()) {
        if (now - item.createdAt > 30 * 60 * 1000) {
          this.cache.delete(key);
        }
      }
    }, 10 * 60 * 1000);
  }

  private savePreviewCache(sekolahId: number, entitas: string, mode: string, rows: PreviewRowResult[]): string {
    const token = crypto.randomUUID();
    this.cache.set(token, { token, sekolahId, entitas, mode, rows, createdAt: Date.now() });
    return token;
  }

  private getPreviewCache(token: string): CacheItem {
    const item = this.cache.get(token);
    if (!item) throw new BadRequestException('Preview token kadaluarsa atau tidak ditemukan');
    return item;
  }

  private async getRowsFromFile(file: UploadedFileType): Promise<{ rowNumber: number; data: Record<string, any> }[]> {
    const ext = file.originalname.split('.').pop()?.toLowerCase();
    if (ext === 'csv') return parseCsv(file.buffer);
    if (ext === 'xlsx' || ext === 'xls') {
      const sheets = await parseExcel(file.buffer);
      return sheets.length > 0 ? sheets[0].rows : [];
    }
    throw new BadRequestException('Format file harus berupa .xlsx, .xls, atau .csv');
  }

  async previewEntity(
    file: UploadedFileType,
    sekolahId: number,
    entitas: 'guru' | 'kelas' | 'mapel' | 'jadwal' | 'siswa' | 'config' | 'pengampu',
    mode: string = 'upsert',
  ): Promise<PreviewResponse> {
    const rawRows = await this.getRowsFromFile(file);
    let schema = GURU_SCHEMA;
    if (entitas === 'kelas') schema = KELAS_SCHEMA;
    if (entitas === 'mapel') schema = MAPEL_SCHEMA;
    if (entitas === 'jadwal') schema = JADWAL_SCHEMA;
    if (entitas === 'siswa') schema = SISWA_SCHEMA;
    if (entitas === 'config') schema = CONFIG_SCHEMA;
    if (entitas === 'pengampu') schema = PENGAMPU_SCHEMA;

    const seenKeys = new Set<string>();
    const results: PreviewRowResult[] = [];
    let validCount = 0, invalidCount = 0, duplicateCount = 0;

    for (const raw of rawRows) {
      const valRes = validateRow(raw.data, schema, raw.rowNumber);
      const rowErrors = valRes.errors.map((e) => e.message);
      const uniqueKeyVal = valRes.data['Kode Guru'] || valRes.data['Kode Mapel'] || valRes.data['Nama Kelas'] || valRes.data['NIP'];

      let status: 'VALID' | 'INVALID' | 'DUPLICATE' = valRes.valid ? 'VALID' : 'INVALID';
      if (uniqueKeyVal) {
        if (seenKeys.has(String(uniqueKeyVal).toLowerCase())) {
          status = 'DUPLICATE';
          rowErrors.push(`Duplikat di dalam file: ${uniqueKeyVal}`);
        } else {
          seenKeys.add(String(uniqueKeyVal).toLowerCase());
        }
      }

      if (status === 'VALID') validCount++;
      else if (status === 'DUPLICATE') duplicateCount++;
      else invalidCount++;

      results.push({ rowNumber: raw.rowNumber, status, data: valRes.data, errors: rowErrors });
    }

    const previewToken = this.savePreviewCache(sekolahId || 1, entitas, mode, results);
    return {
      totalRows: rawRows.length,
      validRows: validCount,
      invalidRows: invalidCount,
      duplicateRows: duplicateCount,
      previewToken,
      rows: results,
      entitas,
      sekolahId: sekolahId || 1,
      mode,
    };
  }

  async previewAll(file: UploadedFileType, sekolahId: number, mode: string = 'upsert'): Promise<Record<string, PreviewResponse>> {
    const sheets = await parseExcel(file.buffer);
    const result: Record<string, PreviewResponse> = {};

    for (const sheet of sheets) {
      const sName = sheet.sheetName.toLowerCase();
      let entitas: 'guru' | 'kelas' | 'mapel' | 'jadwal' | 'siswa' | 'config' | 'pengampu' | null = null;
      if (sName.includes('guru')) entitas = 'guru';
      else if (sName.includes('kelas')) entitas = 'kelas';
      else if (sName.includes('mapel')) entitas = 'mapel';
      else if (sName.includes('jadwal')) entitas = 'jadwal';
      else if (sName.includes('pengaturan') || sName.includes('config')) entitas = 'config';
      else if (sName.includes('pengampu')) entitas = 'pengampu';

      if (entitas) {
        const fakeFile: UploadedFileType = { ...file, buffer: file.buffer, originalname: `${sheet.sheetName}.xlsx` };
        result[entitas] = await this.previewEntity(fakeFile, sekolahId, entitas, mode);
      }
    }

    return result;
  }

  private parseDaysString(str?: string): number[] {
    if (!str) return [];
    const dayMap: Record<string, number> = {
      senin: 1, selasa: 2, rabu: 3, kamis: 4, jumat: 5, sabtu: 6, minggu: 7,
      mon: 1, tue: 2, wed: 3, thu: 4, fri: 5, sat: 6, sun: 7,
    };
    const parts = String(str).split(/[,;/|\s]+/).map((p) => p.trim().toLowerCase()).filter(Boolean);
    const result: number[] = [];
    for (const part of parts) {
      if (dayMap[part]) {
        result.push(dayMap[part]);
      } else {
        const num = parseInt(part, 10);
        if (!isNaN(num) && num >= 1 && num <= 7) {
          result.push(num);
        }
      }
    }
    return Array.from(new Set(result));
  }

  private async autoIntegrateSchoolData(tx: any, targetSekolahId: number) {
    // 1. Ensure SchoolConfig
    let config = await tx.schoolConfig.findUnique({ where: { id_sekolah: targetSekolahId } });
    if (!config) {
      config = await tx.schoolConfig.create({
        data: {
          id_sekolah: targetSekolahId,
          school_days: 5,
          start_time: new Date('1970-01-01T07:00:00Z'),
          duration_per_jp: 35,
          has_routine: true,
          has_monday_ceremony: true,
        },
      });
    }

    // Auto-detect if parallel classes exist (e.g., classes having letters 7A, 7B or multiple classes in same tingkatan)
    const kelases = await tx.kelas.findMany({ where: { id_sekolah: targetSekolahId } });
    const tingkatanMap = new Map<number, number>();
    let detectedParallel = false;

    for (const k of kelases) {
      const currentCount = tingkatanMap.get(k.id_tingkatan) || 0;
      tingkatanMap.set(k.id_tingkatan, currentCount + 1);
      if (currentCount + 1 > 1 || /[A-Za-z]/.test(k.nama_kelas)) {
        detectedParallel = true;
      }
    }

    if (detectedParallel && !config.is_parallel) {
      await tx.schoolConfig.update({
        where: { id_sekolah: targetSekolahId },
        data: { is_parallel: true },
      });
    }

    // 2. Ensure Routine Activities (Breaks)
    const routineCount = await tx.routineActivity.count({ where: { id_sekolah: targetSekolahId } });
    if (routineCount === 0) {
      await tx.routineActivity.createMany({
        data: [
          { id_sekolah: targetSekolahId, name: 'Istirahat 1', time_before_jp: 4, duration: 20, is_active: true },
          { id_sekolah: targetSekolahId, name: 'Istirahat 2', time_before_jp: 6, duration: 20, is_active: true },
        ],
      });
    }

    // 3. Auto-populate JpPerHari for all classes (7 JP / day for school_days)
    const maxDays = config.school_days || 5;
    for (const k of kelases) {
      for (let hari = 1; hari <= maxDays; hari++) {
        const existingJp = await tx.jpPerHari.findFirst({
          where: { id_sekolah: targetSekolahId, id_kelas: k.id, hari },
        });
        if (!existingJp) {
          await tx.jpPerHari.create({
            data: { id_sekolah: targetSekolahId, id_kelas: k.id, hari, jp: 7 },
          });
        }
      }
    }

    // 4. Auto-populate MapelTingkatan (Allocation per subject per grade level)
    const mapels = await tx.mapel.findMany({ where: { id_sekolah: targetSekolahId } });
    const tingkatans = await tx.tingkatan.findMany({ where: { id_sekolah: targetSekolahId } });
    for (const m of mapels) {
      for (const t of tingkatans) {
        const existingMt = await tx.mapelTingkatan.findFirst({
          where: { id_sekolah: targetSekolahId, id_mapel: m.id, id_tingkatan: t.id },
        });
        if (!existingMt) {
          await tx.mapelTingkatan.create({
            data: {
              id_sekolah: targetSekolahId,
              id_mapel: m.id,
              id_tingkatan: t.id,
              jp_per_minggu: m.prioritas ? 5 : 3,
            },
          });
        }
      }
    }

    // 5. Auto-populate Pengampu (Teacher Assignment to Subjects & Classes)
    const gurus = await tx.guru.findMany({ where: { id_sekolah: targetSekolahId } });
    if (gurus.length > 0 && mapels.length > 0 && kelases.length > 0) {
      let guruIndex = 0;
      for (const k of kelases) {
        for (const m of mapels) {
          const existingP = await tx.pengampu.findFirst({
            where: { id_sekolah: targetSekolahId, id_kelas: k.id, id_mapel: m.id },
          });
          if (!existingP) {
            const matchedGuru = gurus.find((g: any) =>
              g.nama.toLowerCase().includes(m.nama.toLowerCase()) || m.nama.toLowerCase().includes(g.nama.toLowerCase())
            ) || gurus[guruIndex % gurus.length];

            await tx.pengampu.create({
              data: {
                id_sekolah: targetSekolahId,
                id_guru: matchedGuru.id,
                id_mapel: m.id,
                id_kelas: k.id,
              },
            });
            guruIndex++;
          }
        }
      }
    }
  }

  async commit(tokensInput: string | string[], sekolahId?: number) {
    const tokens = Array.isArray(tokensInput) ? tokensInput : [tokensInput];
    if (tokens.length === 0) {
      throw new BadRequestException('previewToken wajib diisi');
    }

    const caches: CacheItem[] = tokens.map((t) => this.getPreviewCache(t));
    const targetSekolahId = caches[0].sekolahId || sekolahId || 1;

    let totalInserted = 0;
    let totalSkipped = 0;
    const startTime = Date.now();

    try {
      await this.prisma.$transaction(
        async (tx) => {
          for (const cache of caches) {
            const validRows = cache.rows.filter((r) => r.status === 'VALID').map((r) => r.data);
            if (validRows.length === 0) continue;

            // Cascade deletion for replace mode
            if (cache.mode === 'replace') {
              if (cache.entitas === 'guru') {
                await tx.user.updateMany({ where: { id_sekolah: targetSekolahId }, data: { id_guru: null } });
                await tx.jadwal.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.pengampu.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.waliKelas.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.guruAvailability.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.guru.deleteMany({ where: { id_sekolah: targetSekolahId } });
              } else if (cache.entitas === 'kelas') {
                await tx.jadwal.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.pengampu.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.waliKelas.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.jpPerHari.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.scheduleSlot.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.kelas.deleteMany({ where: { id_sekolah: targetSekolahId } });
              } else if (cache.entitas === 'mapel') {
                await tx.jadwal.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.pengampu.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.mapelTingkatan.deleteMany({ where: { id_sekolah: targetSekolahId } });
                await tx.mapel.deleteMany({ where: { id_sekolah: targetSekolahId } });
              } else if (cache.entitas === 'jadwal') {
                await tx.jadwal.deleteMany({ where: { id_sekolah: targetSekolahId } });
              }
            }

            if (cache.entitas === 'config') {
              for (const r of validRows) {
                try {
                  const days = Number(r['Jumlah Hari Sekolah']) || 5;
                  const duration = Number(r['Durasi Per JP (Menit)']) || 35;
                  const isParallel = String(r['Sekolah Paralel'] || '').toUpperCase() === 'YA';
                  const ceremony = String(r['Ada Upacara Senin'] || '').toUpperCase() !== 'TIDAK';

                  const existingConfig = await tx.schoolConfig.findUnique({ where: { id_sekolah: targetSekolahId } });
                  if (existingConfig) {
                    await tx.schoolConfig.update({
                      where: { id_sekolah: targetSekolahId },
                      data: { school_days: days, duration_per_jp: duration, is_parallel: isParallel, has_monday_ceremony: ceremony },
                    });
                  } else {
                    await tx.schoolConfig.create({
                      data: { id_sekolah: targetSekolahId, school_days: days, duration_per_jp: duration, is_parallel: isParallel, has_monday_ceremony: ceremony },
                    });
                  }
                  totalInserted++;
                } catch (rowErr) {
                  console.error('Row commit error (config):', rowErr);
                }
              }
            } else if (cache.entitas === 'guru') {
              for (const r of validRows) {
                try {
                  const nama = String(r['Nama'] || r['Nama Lengkap'] || '').trim();
                  if (!nama) continue;
                  const nip = r['NIP'] ? String(r['NIP']).trim() : null;
                  const unavailStr = r['Hari Tidak Available'] || r['Hari Off'] || null;

                  let guru = await tx.guru.findFirst({
                    where: {
                      id_sekolah: targetSekolahId,
                      OR: [{ nama }, ...(nip ? [{ nip }] : [])],
                    },
                  });

                  if (!guru) {
                    guru = await tx.guru.create({
                      data: { id_sekolah: targetSekolahId, nama, nip },
                    });
                    totalInserted++;
                  } else if (cache.mode === 'upsert') {
                    guru = await tx.guru.update({
                      where: { id: guru.id },
                      data: { nama, nip },
                    });
                    totalInserted++;
                  }

                  // Handle Guru Availability
                  if (unavailStr && guru) {
                    const days = this.parseDaysString(String(unavailStr));
                    for (const day of days) {
                      const existingAvail = await tx.guruAvailability.findFirst({
                        where: { id_sekolah: targetSekolahId, id_guru: guru.id, hari: day },
                      });
                      if (!existingAvail) {
                        await tx.guruAvailability.create({
                          data: { id_sekolah: targetSekolahId, id_guru: guru.id, hari: day },
                        });
                      }
                    }
                  }
                } catch (rowErr) {
                  console.error('Row commit error (guru):', rowErr);
                }
              }
            } else if (cache.entitas === 'kelas') {
              // Helper: auto-compute Fase from Tingkat (SD-only: 1-2→A, 3-4→B, 5-6→C)
              const faseFromTingkat = (t: number): string => {
                if (t <= 2) return 'A';
                if (t <= 4) return 'B';
                if (t <= 6) return 'C';
                if (t <= 9) return 'D';
                if (t === 10) return 'E';
                return 'F';
              };

              for (const r of validRows) {
                try {
                  const namaKelas = String(r['Nama Kelas'] || '').trim();
                  if (!namaKelas) continue;
                  const tingkatNum = Number(r['Tingkat']) || 1;
                  const faseKelas = faseFromTingkat(tingkatNum);
                  const waliVal = r['Wali Kelas'] ? String(r['Wali Kelas']).trim() : null;

                  // Always match tingkatan by exact level number
                  let tingkatan = await tx.tingkatan.findFirst({
                    where: { id_sekolah: targetSekolahId, nama: `Tingkat ${tingkatNum}` },
                  });
                  if (!tingkatan) {
                    tingkatan = await tx.tingkatan.create({
                      data: { id_sekolah: targetSekolahId, nama: `Tingkat ${tingkatNum}` },
                    });
                  }

                  let existing = await tx.kelas.findFirst({
                    where: { id_sekolah: targetSekolahId, nama_kelas: namaKelas },
                  });

                  if (!existing) {
                    existing = await tx.kelas.create({
                      data: {
                        id_sekolah: targetSekolahId,
                        id_tingkatan: tingkatan.id,
                        nama_kelas: namaKelas,
                        kode_lengkap: namaKelas,
                        fase: faseKelas,
                      },
                    });
                    totalInserted++;
                  } else if (cache.mode === 'upsert') {
                    existing = await tx.kelas.update({
                      where: { id: existing.id },
                      data: { nama_kelas: namaKelas, id_tingkatan: tingkatan.id, fase: faseKelas },
                    });
                    totalInserted++;
                  }

                  // Handle Wali Kelas
                  if (waliVal && existing) {
                    const gWali = await tx.guru.findFirst({
                      where: {
                        id_sekolah: targetSekolahId,
                        OR: [
                          { nama: waliVal },
                          { nama: { contains: waliVal } },
                          { nip: waliVal },
                        ],
                      },
                    });

                    if (gWali) {
                      await tx.kelas.update({
                        where: { id: existing.id },
                        data: { id_wali_kelas: gWali.id },
                      });

                      const existingWali = await tx.waliKelas.findFirst({
                        where: { id_sekolah: targetSekolahId, id_kelas: existing.id },
                      });
                      if (!existingWali) {
                        await tx.waliKelas.create({
                          data: { id_sekolah: targetSekolahId, id_guru: gWali.id, id_kelas: existing.id },
                        });
                      } else {
                        await tx.waliKelas.update({
                          where: { id: existingWali.id },
                          data: { id_guru: gWali.id },
                        });
                      }
                    }
                  }
                } catch (rowErr) {
                  console.error('Row commit error (kelas):', rowErr);
                }
              }
            } else if (cache.entitas === 'mapel') {
              for (const r of validRows) {
                try {
                  const namaMapel = String(r['Nama Mapel'] || '').trim();
                  if (!namaMapel) continue;
                  const isPrioritas = String(r['Kategori'] || '').toUpperCase() === 'WAJIB';
                  const jamPerMinggu = Number(r['Jam Per Minggu']) || (isPrioritas ? 5 : 3);

                  let mapel = await tx.mapel.findFirst({
                    where: { id_sekolah: targetSekolahId, nama: namaMapel },
                  });

                  if (!mapel) {
                    mapel = await tx.mapel.create({
                      data: { id_sekolah: targetSekolahId, nama: namaMapel, prioritas: isPrioritas },
                    });
                    totalInserted++;
                  } else if (cache.mode === 'upsert') {
                    mapel = await tx.mapel.update({
                      where: { id: mapel.id },
                      data: { nama: namaMapel, prioritas: isPrioritas },
                    });
                    totalInserted++;
                  }

                  // Update MapelTingkatan with specified Jam Per Minggu
                  const allTingkatans = await tx.tingkatan.findMany({ where: { id_sekolah: targetSekolahId } });
                  for (const t of allTingkatans) {
                    const existingMt = await tx.mapelTingkatan.findFirst({
                      where: { id_sekolah: targetSekolahId, id_mapel: mapel.id, id_tingkatan: t.id },
                    });
                    if (!existingMt) {
                      await tx.mapelTingkatan.create({
                        data: {
                          id_sekolah: targetSekolahId,
                          id_mapel: mapel.id,
                          id_tingkatan: t.id,
                          jp_per_minggu: jamPerMinggu,
                        },
                      });
                    } else if (cache.mode === 'upsert' || cache.mode === 'replace') {
                      await tx.mapelTingkatan.update({
                        where: { id: existingMt.id },
                        data: { jp_per_minggu: jamPerMinggu },
                      });
                    }
                  }
                } catch (rowErr) {
                  console.error('Row commit error (mapel):', rowErr);
                }
              }
            } else if (cache.entitas === 'pengampu') {
              for (const r of validRows) {
                try {
                  const namaKelas = String(r['Kelas'] || '').trim();
                  const namaMapel = String(r['Mapel'] || '').trim();
                  const namaGuru = String(r['Guru'] || '').trim();

                  const k = await tx.kelas.findFirst({
                    where: { id_sekolah: targetSekolahId, OR: [{ nama_kelas: namaKelas }, { kode_lengkap: namaKelas }] },
                  });
                  const m = await tx.mapel.findFirst({
                    where: { id_sekolah: targetSekolahId, OR: [{ nama: namaMapel }, { nama: { contains: namaMapel } }] },
                  });
                  const g = await tx.guru.findFirst({
                    where: { id_sekolah: targetSekolahId, OR: [{ nama: namaGuru }, { nip: namaGuru }] },
                  });

                  if (k && m && g) {
                    const existingP = await tx.pengampu.findFirst({
                      where: { id_sekolah: targetSekolahId, id_kelas: k.id, id_mapel: m.id },
                    });
                    if (!existingP) {
                      await tx.pengampu.create({
                        data: { id_sekolah: targetSekolahId, id_guru: g.id, id_mapel: m.id, id_kelas: k.id },
                      });
                      totalInserted++;
                    } else {
                      await tx.pengampu.update({
                        where: { id: existingP.id },
                        data: { id_guru: g.id },
                      });
                      totalInserted++;
                    }
                  }
                } catch (rowErr) {
                  console.error('Row commit error (pengampu):', rowErr);
                }
              }
            } else if (cache.entitas === 'jadwal') {
              const dayMap: Record<string, number> = {
                SENIN: 1, SELASA: 2, RABU: 3, KAMIS: 4, JUMAT: 5, SABTU: 6,
              };

              let activePeriode = await tx.periodeJadwal.findFirst({
                where: { id_sekolah: targetSekolahId, is_active: true },
              });
              if (!activePeriode) {
                activePeriode = await tx.periodeJadwal.create({
                  data: {
                    id_sekolah: targetSekolahId,
                    nama: 'Periode Utama',
                    semester: 'Gasal',
                    tahun_ajaran: '2026/2027',
                    is_active: true,
                  },
                });
              }

              for (const r of validRows) {
                try {
                  const hariNum = dayMap[String(r['Hari']).toUpperCase()] || 1;
                  const jamKe = Number(r['Jam Ke']) || 1;
                  const namaKelas = String(r['Kelas']).trim();
                  const namaMapel = String(r['Mapel']).trim();
                  const namaGuru = String(r['Guru']).trim();

                  const k = await tx.kelas.findFirst({
                    where: {
                      id_sekolah: targetSekolahId,
                      OR: [{ nama_kelas: namaKelas }, { kode_lengkap: namaKelas }],
                    },
                  });
                  const m = await tx.mapel.findFirst({
                    where: {
                      id_sekolah: targetSekolahId,
                      OR: [{ nama: namaMapel }, { nama: { contains: namaMapel } }],
                    },
                  });
                  const g = await tx.guru.findFirst({
                    where: {
                      id_sekolah: targetSekolahId,
                      OR: [{ nama: namaGuru }, { nip: namaGuru }],
                    },
                  });

                  if (k && m && g) {
                    await tx.jadwal.create({
                      data: {
                        id_sekolah: targetSekolahId,
                        id_periode_jadwal: activePeriode.id,
                        id_kelas: k.id,
                        id_mapel: m.id,
                        id_guru: g.id,
                        hari: hariNum,
                        jam_ke: jamKe,
                      },
                    });
                    totalInserted++;
                  }
                } catch (rowErr) {
                  console.error('Row commit error (jadwal):', rowErr);
                }
              }
            }

            totalSkipped += cache.rows.length - validRows.length;
          }

          // Full System Auto-Integration: Automatically generate and link all missing relations so user can click "Generate" instantly
          await this.autoIntegrateSchoolData(tx, targetSekolahId);
        },
        { timeout: 40000 },
      );

      for (const t of tokens) {
        this.cache.delete(t);
      }

      return {
        inserted: totalInserted,
        skipped: totalSkipped,
        duration: `${((Date.now() - startTime) / 1000).toFixed(2)}s`,
      };
    } catch (err: any) {
      console.error('Error during commit:', err);
      throw new InternalServerErrorException(err.message || 'Gagal menyimpan data ke database');
    }
  }
}
