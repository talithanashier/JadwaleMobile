import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class SekolahService {
  constructor(private prisma: PrismaService) {}

  async getProfile(id_sekolah: number) {
    return this.prisma.sekolah.findUnique({
      where: { id: id_sekolah },
      include: { config: true },
    });
  }

  async getStats(id_sekolah: number) {
    const guruCount = await this.prisma.guru.count({ where: { id_sekolah, deleted_at: null } });
    const kelasCount = await this.prisma.kelas.count({ where: { id_sekolah, deleted_at: null } });
    const mapelCount = await this.prisma.mapel.count({ where: { id_sekolah, deleted_at: null } });
    const jadwalCount = await this.prisma.jadwal.count({ where: { id_sekolah } });

    return {
      guru: guruCount,
      kelas: kelasCount,
      mapel: mapelCount,
      jadwal: jadwalCount,
    };
  }

  async updateProfile(id_sekolah: number, data: any) {
    const { config, ...sekolahData } = data;
    
    // Update sekolah
    if (Object.keys(sekolahData).length > 0) {
      await this.prisma.sekolah.update({
        where: { id: id_sekolah },
        data: sekolahData,
      });
    }

    // Update config
    if (config) {
      await this.prisma.schoolConfig.upsert({
        where: { id_sekolah },
        update: config,
        create: { ...config, id_sekolah },
      });
    }

    return this.getProfile(id_sekolah);
  }

  async hydrate(id_sekolah: number, data: any) {
    return this.prisma.$transaction(async (tx) => {
      // 1. Update School Config
      const startTime = data.start_time ? new Date(`1970-01-01T${data.start_time}:00Z`) : new Date(`1970-01-01T07:00:00Z`);
      const ist1 = data.istirahat?.[0];
      const ist2 = data.istirahat?.[1];

      await tx.schoolConfig.upsert({
        where: { id_sekolah },
        update: {
          is_parallel: data.is_parallel,
          class_naming: data.class_naming,
          school_days: data.school_days,
          start_time: startTime,
          duration_per_jp: data.duration_per_jp,
          has_routine: data.has_routine || false,
          routine_duration: data.routine_duration || 15,
          has_monday_ceremony: data.has_monday_ceremony,
          break1_duration: ist1 ? Number(ist1.duration) : 15,
          break1_after_jp: ist1 ? Number(ist1.after_jp) : 3,
          break2_duration: ist2 ? Number(ist2.duration) : 15,
          break2_after_jp: ist2 ? Number(ist2.after_jp) : 5,
        },
        create: {
          id_sekolah,
          is_parallel: data.is_parallel,
          class_naming: data.class_naming,
          school_days: data.school_days,
          start_time: startTime,
          duration_per_jp: data.duration_per_jp,
          has_routine: data.has_routine || false,
          routine_duration: data.routine_duration || 15,
          has_monday_ceremony: data.has_monday_ceremony,
          break1_duration: ist1 ? Number(ist1.duration) : 15,
          break1_after_jp: ist1 ? Number(ist1.after_jp) : 3,
          break2_duration: ist2 ? Number(ist2.duration) : 15,
          break2_after_jp: ist2 ? Number(ist2.after_jp) : 5,
        }
      });

      // Clear existing master data for hydration
      await tx.routineActivity.deleteMany({ where: { id_sekolah } });
      await tx.pengampu.deleteMany({ where: { id_sekolah } });
      await tx.waliKelas.deleteMany({ where: { id_sekolah } });
      await tx.jpPerHari.deleteMany({ where: { id_sekolah } });
      await tx.mapelTingkatan.deleteMany({ where: { id_sekolah } });
      await tx.kelas.deleteMany({ where: { id_sekolah } });
      await tx.tingkatan.deleteMany({ where: { id_sekolah } });
      await tx.mapel.deleteMany({ where: { id_sekolah } });
      await tx.guruAvailability.deleteMany({ where: { id_sekolah } });
      await tx.guru.deleteMany({ where: { id_sekolah } });

      // 2. Recreate Routine / Istirahat
      const routines = [];
      if (data.has_routine) {
        routines.push({
          id_sekolah,
          name: 'Kegiatan Pembiasaan',
          time_before_jp: 1, // Before JP 1
          duration: data.routine_duration || 15,
        });
      }
      if (data.istirahat && data.istirahat.length > 0) {
        data.istirahat.forEach((ist: any, idx: number) => {
          routines.push({
            id_sekolah,
            name: `Istirahat ${idx + 1}`,
            time_before_jp: ist.after_jp + 1,
            duration: ist.duration,
          });
        });
      }
      if (routines.length > 0) {
        await tx.routineActivity.createMany({ data: routines });
      }

      // 3. Mapels & Tingkatan
      const mapelMap = new Map<string, number>();
      for (const m of data.mapels || []) {
        const created = await tx.mapel.create({
          data: {
            id_sekolah,
            nama: m.nama,
            prioritas: m.prioritas,
            color: m.color,
          }
        });
        mapelMap.set(m.id, created.id);
      }

      // We need to create tingkatan FIRST before MapelTingkatan
      const tingkatanMap = new Map<number, number>();
      const kelasMap = new Map<string, number>();
      const tingkatanCount = data.tingkatan_count || 6;
      const kelasPerTingkatan = data.kelas_per_tingkatan || 1;

      for (let t = 1; t <= tingkatanCount; t++) {
        const tingkatan = await tx.tingkatan.create({
          data: { id_sekolah, nama: `Kelas ${t}` }
        });
        tingkatanMap.set(t, tingkatan.id);

        if (!data.is_parallel) {
          const nama_kelas = `Kelas ${t}`;
          const kls = await tx.kelas.create({
            data: { id_sekolah, id_tingkatan: tingkatan.id, nama_kelas, kode_lengkap: `${t}` }
          });
          kelasMap.set(`${t}`, kls.id);
        } else {
          for (let k = 0; k < kelasPerTingkatan; k++) {
            const suffix = data.class_naming === 'alphabet' ? String.fromCharCode(65 + k) : `-${k + 1}`;
            const nama_kelas = `${t}${suffix}`;
            const kls = await tx.kelas.create({
              data: { id_sekolah, id_tingkatan: tingkatan.id, nama_kelas, kode_lengkap: nama_kelas }
            });
            kelasMap.set(nama_kelas, kls.id);
          }
        }
      }

      // Now insert MapelTingkatan
      const mapelTingkatans = [];
      for (const m of data.mapels || []) {
        const id_mapel = mapelMap.get(m.id);
        if (id_mapel && m.jp_per_tingkatan) {
          for (const [t_str, jp] of Object.entries(m.jp_per_tingkatan)) {
            const t = parseInt(t_str);
            const id_tingkatan = tingkatanMap.get(t);
            const jpNum = Number(jp);
            if (id_tingkatan && jpNum > 0) {
              mapelTingkatans.push({ id_sekolah, id_mapel, id_tingkatan, jp_per_minggu: jpNum });
            }
          }
        }
      }
      if (mapelTingkatans.length > 0) {
        await tx.mapelTingkatan.createMany({ data: mapelTingkatans });
      }

      // 4. Guru & Availability
      const guruMap = new Map<string, number>();
      for (const g of data.gurus || []) {
        const created = await tx.guru.create({
          data: { id_sekolah, nama: g.nama, nip: g.nip || null }
        });
        guruMap.set(g.id, created.id);

        if (g.availability && g.availability.length > 0) {
           await tx.guruAvailability.createMany({
             data: g.availability.map((a: any) => ({
               id_sekolah,
               id_guru: created.id,
               hari: a.hari,
               jam_mulai: a.jam_mulai ? new Date(`1970-01-01T${a.jam_mulai}:00Z`) : null,
               jam_selesai: a.jam_selesai ? new Date(`1970-01-01T${a.jam_selesai}:00Z`) : null,
             }))
           });
        }
      }

      // 5. Jp Per Hari
      if (data.jp_per_hari) {
        const jpPerHariData = [];
        for (const [key, jp] of Object.entries(data.jp_per_hari)) {
          const [cid, hariStr] = key.split('_');
          const id_kelas = kelasMap.get(cid);
          const hari = parseInt(hariStr);
          const jpNum = Number(jp);
          if (id_kelas && hari && jpNum > 0) {
            jpPerHariData.push({ id_sekolah, id_kelas, hari, jp: jpNum });
          }
        }
        if (jpPerHariData.length > 0) {
          await tx.jpPerHari.createMany({ data: jpPerHariData });
        }
      }

      // 6. Wali Kelas & Pengampu (Default Mapels)
      const pengampuData = [];
      if (data.wali_kelas) {
        for (const wk of data.wali_kelas) {
           const id_kelas = kelasMap.get(wk.id_kelas);
           const id_guru = guruMap.get(wk.id_guru);
           if (id_kelas && id_guru) {
             await tx.waliKelas.create({ data: { id_sekolah, id_kelas, id_guru } });
             
             // Convert default_mapels to Pengampu
             if (wk.default_mapels && wk.default_mapels.length > 0) {
               for (const mid of wk.default_mapels) {
                 const id_mapel = mapelMap.get(mid);
                 if (id_mapel) {
                   pengampuData.push({ id_sekolah, id_guru, id_mapel, id_kelas });
                 }
               }
             }
           }
        }
      }

      if (data.pengampus) {
        for (const p of data.pengampus) {
           const id_guru = guruMap.get(p.id_guru);
           const id_mapel = mapelMap.get(p.id_mapel);
           if (id_guru && id_mapel && p.class_ids) {
              for (const cid of p.class_ids) {
                 const id_kelas = kelasMap.get(cid);
                 // Only add if not already added by default_mapels
                 const exists = pengampuData.find(pd => pd.id_guru === id_guru && pd.id_mapel === id_mapel && pd.id_kelas === id_kelas);
                 if (id_kelas && !exists) {
                   pengampuData.push({ id_sekolah, id_guru, id_mapel, id_kelas });
                 }
              }
           }
        }
      }
      
      if (pengampuData.length > 0) {
        await tx.pengampu.createMany({ data: pengampuData });
      }

      return { status: 'success', message: 'Data wizard berhasil disimpan' };
    });
  }
}
