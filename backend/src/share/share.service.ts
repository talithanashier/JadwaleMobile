import { Injectable, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class ShareService {
  constructor(private prisma: PrismaService) {}

  async createLink(data: {
    id_sekolah: number;
    created_by: number;
    id_kelas?: number;
    permission?: string;
    allowed_user_id?: number;
    expires_in_days?: number;
  }) {
    const uuid = uuidv4();
    const expires_at = data.expires_in_days 
      ? new Date(Date.now() + data.expires_in_days * 24 * 60 * 60 * 1000) 
      : null;

    const link = await this.prisma.sharedLink.create({
      data: {
        id_sekolah: data.id_sekolah,
        uuid,
        id_kelas: data.id_kelas,
        permission: data.permission || 'read',
        created_by: data.created_by,
        allowed_user_id: data.allowed_user_id,
        expires_at
      }
    });

    return { url: `/shared/${uuid}`, uuid };
  }

  async getSharedData(uuid: string) {
    const link = await this.prisma.sharedLink.findUnique({
      where: { uuid },
      include: { sekolah: true }
    });

    if (!link) {
      throw new NotFoundException('Tautan tidak ditemukan.');
    }

    if (link.expires_at && link.expires_at < new Date()) {
      throw new UnauthorizedException('Tautan ini telah kedaluwarsa.');
    }

    // Increment view count (fire and forget)
    this.prisma.sharedLink.update({
      where: { id: link.id },
      data: { view_count: { increment: 1 } }
    }).catch(e => console.error(e));

    const config = await this.prisma.schoolConfig.findUnique({ where: { id_sekolah: link.id_sekolah } });
    const routines = await this.prisma.routineActivity.findMany({ 
      where: { id_sekolah: link.id_sekolah }, 
      orderBy: { time_before_jp: 'asc' } 
    });

    const where: any = { id_sekolah: link.id_sekolah };
    if (link.id_kelas) where.id_kelas = link.id_kelas;

    const jadwal = await this.prisma.jadwal.findMany({
      where,
      include: { kelas: true, mapel: true, guru: true },
      orderBy: [{ hari: 'asc' }, { jam_ke: 'asc' }]
    });

    return {
      sekolah: {
        id: link.sekolah.id,
        nama: link.sekolah.nama_sekolah
      },
      config: config || {
        start_time: '1970-01-01T07:00:00.000Z',
        duration_per_jp: 45,
        school_days: 5,
        has_monday_ceremony: true,
      },
      routines: routines || [],
      permission: link.permission,
      jadwal
    };
  }
}
