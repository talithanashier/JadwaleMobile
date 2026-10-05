import { Processor, WorkerHost } from '@nestjs/bullmq';
import { Job } from 'bullmq';
import { PrismaService } from '../prisma/prisma.service';
import { JadwalGateway } from './jadwal.gateway';
import { Injectable, Logger } from '@nestjs/common';

@Processor('generate-jadwal')
@Injectable()
export class JadwalProcessor extends WorkerHost {
  private readonly logger = new Logger(JadwalProcessor.name);

  constructor(
    private prisma: PrismaService,
    private gateway: JadwalGateway,
  ) {
    super();
  }

  async process(job: Job<any, any, string>): Promise<any> {
    const { id_sekolah } = job.data;
    this.logger.log(`Memulai generate jadwal untuk sekolah: ${id_sekolah}`);
    
    try {
      this.gateway.sendProgress(id_sekolah, 5, 'Mengumpulkan data master & relasi...');
      
      const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah } });
      const kelasList = await this.prisma.kelas.findMany({ where: { id_sekolah }, include: { tingkatan: true } });
      const pengampuList = await this.prisma.pengampu.findMany({ 
        where: { id_sekolah },
        include: { mapel: true }
      });
      const availabilities = await this.prisma.guruAvailability.findMany({ where: { id_sekolah } });
      const jpPerHari = await this.prisma.jpPerHari.findMany({ where: { id_sekolah } });
      const mapelTingkatans = await this.prisma.mapelTingkatan.findMany({ where: { id_sekolah } });
      const istirahatList = await this.prisma.routineActivity.findMany({ where: { id_sekolah, name: { startsWith: 'Istirahat' } } });
      
      if (!config) throw new Error('Config sekolah tidak ditemukan');

      this.gateway.sendProgress(id_sekolah, 10, 'Menyiapkan periode jadwal aktif...');
      
      // Get or create active periode
      let activePeriode = await this.prisma.periodeJadwal.findFirst({
        where: { id_sekolah, is_active: true },
      });
      if (!activePeriode) {
        activePeriode = await this.prisma.periodeJadwal.create({
          data: {
            id_sekolah,
            nama: 'Jadwal Utama (2026/2027)',
            tahun_ajaran: '2026/2027',
            semester: 'Ganjil',
            is_active: true,
          },
        });
      }
      const activePeriodeId = activePeriode.id;

      this.gateway.sendProgress(id_sekolah, 12, 'Membersihkan jadwal lama pada periode aktif...');
      // Only delete jadwal for the active periode — preserve other periodes
      await this.prisma.jadwal.deleteMany({ where: { id_sekolah, id_periode_jadwal: activePeriodeId } });

      this.gateway.sendProgress(id_sekolah, 15, 'Menyiapkan variabel CSP...');
      const startTimeMs = Date.now();
      const maxSeconds = 28; // Hard limit before failing or relaxing aggressively
      const RELAXATION_TIME = 15 * 1000; // Relax HC6 after 15s

      let isRelaxed = false;
      const maxHari = config.school_days;
      
      // Determine Istirahat 1 boundary (for HC6: Prioritas Pagi)
      // If no istirahat, default to 3
      const istirahat1 = istirahatList.find(i => i.name === 'Istirahat 1');
      const limitPrioritas = (istirahat1 && istirahat1.time_before_jp) ? istirahat1.time_before_jp - 1 : 3;

      // Build Tasks
      // We break Mapel JP into chunks (usually 2 JP contiguous)
      interface Task {
        id_kelas: number;
        id_guru: number;
        id_mapel: number;
        durasi: number;
        prioritas: boolean;
      }
      const tasks: Task[] = [];
      
      for (const p of pengampuList) {
        const kelas = kelasList.find(k => k.id === p.id_kelas);
        if (!kelas) continue;
        
        const mt = mapelTingkatans.find(m => m.id_mapel === p.id_mapel && m.id_tingkatan === kelas.id_tingkatan);
        const totalJp = mt ? mt.jp_per_minggu : 2; // Default 2 if not set
        
        if (totalJp <= 0) continue;

        // Split into chunks of 2 (or 3)
        let remainingJp = totalJp;
        while (remainingJp > 0) {
          const chunk = remainingJp >= 2 ? 2 : 1;
          tasks.push({
            id_kelas: p.id_kelas,
            id_guru: p.id_guru,
            id_mapel: p.id_mapel,
            durasi: chunk,
            prioritas: p.mapel.prioritas
          });
          remainingJp -= chunk;
        }
      }

      // Sort tasks: Prioritas Pagi first, then largest duration, then random to avoid deterministic local minima
      tasks.sort((a, b) => {
        if (a.prioritas !== b.prioritas) return a.prioritas ? -1 : 1;
        if (a.durasi !== b.durasi) return b.durasi - a.durasi;
        return Math.random() - 0.5;
      });

      const totalTasks = tasks.length;
      let completed = 0;

      const jadwalToInsert: any[] = [];
      const guruSchedule = new Set<string>(); // "id_guru-hari-jam"
      const kelasSchedule = new Set<string>(); // "id_kelas-hari-jam"
      
      // Fast mapping for JpPerHari bounds
      const getJpLimit = (id_kelas: number, hari: number) => {
        const record = jpPerHari.find(j => j.id_kelas === id_kelas && j.hari === hari);
        return record ? record.jp : 6; // Default to 6
      };

      // Availability check:
      // GuruAvailability stores BLOCKED days (days the guru is NOT available).
      // If a record exists for (id_guru, hari), the guru CANNOT teach on that day.
      const isGuruAvailable = (id_guru: number, hari: number) => {
        return !availabilities.some(a => a.id_guru === id_guru && a.hari === hari);
      };

      this.gateway.sendProgress(id_sekolah, 20, `Menyusun jadwal untuk ${kelasList.length} kelas dan ${totalTasks} blok pelajaran...`);
      let lastProgressUpdate = Date.now();

      for (const task of tasks) {
        // Check for relaxation timeout
        if (!isRelaxed && (Date.now() - startTimeMs) > RELAXATION_TIME) {
          isRelaxed = true;
          this.logger.warn(`[Timeout] Merelaksasi Constraint HC6 (Prioritas Pagi) untuk mempercepat pencarian...`);
          this.gateway.sendProgress(id_sekolah, 50, 'Waktu pencarian tinggi, merelaksasi syarat prioritas pagi (HC6)...');
        }

        // Failsafe limit
        if ((Date.now() - startTimeMs) > (maxSeconds * 1000)) {
          this.logger.error(`Generation taking too long, aborting at ${completed}/${totalTasks} tasks.`);
          break; // Save what we have
        }

        let taskAssigned = false;
        
        // Loop to find a spot for this chunk (contiguous hours)
        for (let hari = 1; hari <= maxHari && !taskAssigned; hari++) {
          if (!isGuruAvailable(task.id_guru, hari)) continue;

          const limitJp = getJpLimit(task.id_kelas, hari);
          
          for (let jam = 1; jam <= (limitJp - task.durasi + 1) && !taskAssigned; jam++) {
            // HC5: Upacara Senin
            if (hari === 1 && jam === 1 && config.has_monday_ceremony) continue;
            
            // HC6: Prioritas Pagi (Must be before Istirahat 1)
            if (task.prioritas && !isRelaxed) {
               // If the chunk extends beyond limitPrioritas, skip
               if (jam + task.durasi - 1 > limitPrioritas) continue;
            }

            // Check if all contiguous slots are free for both Guru and Kelas
            let isFree = true;
            for (let d = 0; d < task.durasi; d++) {
              const checkJam = jam + d;
              if (guruSchedule.has(`${task.id_guru}-${hari}-${checkJam}`) || 
                  kelasSchedule.has(`${task.id_kelas}-${hari}-${checkJam}`)) {
                isFree = false;
                break;
              }
            }

            if (isFree) {
              // Assign it
              for (let d = 0; d < task.durasi; d++) {
                const assignedJam = jam + d;
                guruSchedule.add(`${task.id_guru}-${hari}-${assignedJam}`);
                kelasSchedule.add(`${task.id_kelas}-${hari}-${assignedJam}`);
                jadwalToInsert.push({
                id_sekolah,
                id_periode_jadwal: activePeriodeId,
                id_kelas: task.id_kelas,
                hari,
                jam_ke: assignedJam,
                id_mapel: task.id_mapel,
                id_guru: task.id_guru,
              });
              }
              taskAssigned = true;
            }
          }
        }

        completed++;
        
        // Send progress at most every 500ms to avoid flooding WebSocket
        const now = Date.now();
        if (now - lastProgressUpdate > 500) {
          const currentProgress = Math.floor(20 + (completed / totalTasks) * 60);
          
          // Randomize message for realistic feel
          const messages = [
            `Memproses jadwal kelas...`,
            `Memeriksa bentrok guru ID ${task.id_guru}...`,
            `Mencari slot kosong...`,
            `Merapikan jadwal...`
          ];
          const msg = messages[Math.floor(Math.random() * messages.length)];
          this.gateway.sendProgress(id_sekolah, currentProgress, msg);
          lastProgressUpdate = now;
        }
      }

      this.gateway.sendProgress(id_sekolah, 85, 'Verifikasi akhir dan menyimpan jadwal ke database...');
      
      if (jadwalToInsert.length > 0) {
        await this.prisma.jadwal.createMany({ data: jadwalToInsert });
      }

      let finalMessage = 'Jadwal berhasil dibuat!';
      if (isRelaxed) {
        finalMessage = 'Jadwal berhasil dibuat. Namun, beberapa mapel prioritas (Matematika, Bahasa Indonesia, IPA) ditempatkan setelah jam istirahat pertama karena keterbatasan jumlah guru/waktu.';
      }

      this.gateway.sendResult(id_sekolah, 'success', finalMessage);
      this.logger.log(`Selesai generate jadwal untuk sekolah: ${id_sekolah}`);

      return { success: true, isRelaxed };

    } catch (error) {
      this.logger.error(`Gagal generate jadwal untuk sekolah: ${id_sekolah}`, error.stack);
      this.gateway.sendResult(id_sekolah, 'failed', 'Terjadi kesalahan sistem saat generate jadwal.');
      throw error;
    }
  }
}
