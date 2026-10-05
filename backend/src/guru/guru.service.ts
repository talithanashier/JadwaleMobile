import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class GuruService {
  constructor(private prisma: PrismaService) {}

  async findAll(id_sekolah: number) {
    return this.prisma.guru.findMany({
      where: { id_sekolah, deleted_at: null },
      include: {
        guruAvailabilities: true,
        users: {
          select: {
            id: true,
            nama: true,
            email: true,
            role: true,
            is_verified: true,
            is_active: true,
          }
        }
      }
    });
  }

  async findOne(id: number, id_sekolah: number) {
    return this.prisma.guru.findFirst({
      where: { id, id_sekolah, deleted_at: null },
      include: {
        guruAvailabilities: true,
        users: {
          select: {
            id: true,
            nama: true,
            email: true,
            role: true,
            is_verified: true,
            is_active: true,
          }
        }
      }
    });
  }

  async create(id_sekolah: number, data: any) {
    const { nama, nip } = data;
    return this.prisma.guru.create({
      data: {
        id_sekolah,
        nama,
        nip: nip || null,
      }
    });
  }

  async update(id: number, id_sekolah: number, data: any) {
    const updatePayload: any = {};
    if (data.nama !== undefined) updatePayload.nama = data.nama;
    if (data.nip !== undefined) updatePayload.nip = data.nip || null;

    await this.prisma.guru.updateMany({
      where: { id, id_sekolah },
      data: updatePayload,
    });

    return this.findOne(id, id_sekolah);
  }

  async remove(id: number, id_sekolah: number) {
    return this.prisma.guru.updateMany({
      where: { id, id_sekolah },
      data: { deleted_at: new Date() }
    });
  }

  async setAvailability(id_guru: number, id_sekolah: number, availabilities: any[]) {
    // 1. Delete all existing availabilities for this guru
    await this.prisma.guruAvailability.deleteMany({
      where: { id_guru, id_sekolah }
    });

    // 2. Insert new ones if any
    if (availabilities && availabilities.length > 0) {
      const dataToInsert = availabilities.map(a => ({
        id_sekolah,
        id_guru,
        hari: a.hari,
        jam_mulai: a.jam_mulai ? new Date(a.jam_mulai.includes('T') ? a.jam_mulai : `1970-01-01T${a.jam_mulai}:00Z`) : null,
        jam_selesai: a.jam_selesai ? new Date(a.jam_selesai.includes('T') ? a.jam_selesai : `1970-01-01T${a.jam_selesai}:00Z`) : null
      }));
      await this.prisma.guruAvailability.createMany({
        data: dataToInsert
      });
    }

    return { success: true, message: 'Kehadiran berhasil diperbarui' };
  }

  async getPendingTeachers(id_sekolah: number) {
    return this.prisma.user.findMany({
      where: {
        id_sekolah,
        role: 'TENAGA_PENDIDIK',
        is_verified: false,
      },
      include: {
        guru: true,
      },
      orderBy: { created_at: 'desc' }
    });
  }

  async verifyTeacher(id: number, id_sekolah: number) {
    const user = await this.prisma.user.findFirst({
      where: { id, id_sekolah },
      include: { guru: true }
    });

    if (!user) {
      throw new Error('User Tenaga Pendidik tidak ditemukan di sekolah ini');
    }

    let id_guru = user.id_guru;

    if (!id_guru) {
      // Find or create Guru record
      let guru = await this.prisma.guru.findFirst({
        where: { id_sekolah, nama: user.nama, deleted_at: null }
      });

      if (!guru) {
        guru = await this.prisma.guru.create({
          data: {
            id_sekolah,
            nama: user.nama,
            nip: null,
          }
        });
      }
      id_guru = guru.id;
    }

    return this.prisma.user.update({
      where: { id },
      data: {
        is_verified: true,
        is_active: true,
        id_guru,
      }
    });
  }

  async rejectTeacher(id: number, id_sekolah: number) {
    const user = await this.prisma.user.findFirst({
      where: { id, id_sekolah, role: 'TENAGA_PENDIDIK' },
    });
    if (!user) {
      throw new Error('User Tenaga Pendidik tidak ditemukan di sekolah ini');
    }
    await this.prisma.user.delete({ where: { id } });
    return { success: true, message: 'Pendaftaran Tenaga Pendidik ditolak dan akun dihapus.' };
  }
}

