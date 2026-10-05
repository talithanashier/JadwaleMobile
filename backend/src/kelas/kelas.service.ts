import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class KelasService {
  constructor(private prisma: PrismaService) {}

  async findAll(id_sekolah: number) {
    return this.prisma.kelas.findMany({
      where: { id_sekolah, deleted_at: null },
      include: {
        tingkatan: true,
        waliKelasList: {
          include: { guru: true }
        }
      },
      orderBy: { id: 'asc' }
    });
  }

  async create(id_sekolah: number, data: any) {
    const { id_guru_wali, ...kelasData } = data;
    
    if (kelasData.id_tingkatan) {
      kelasData.id_tingkatan = Number(kelasData.id_tingkatan);
    }

    const kls = await this.prisma.kelas.create({
      data: {
        ...kelasData,
        id_sekolah,
      }
    });

    if (id_guru_wali) {
      const guruId = Number(id_guru_wali);
      await this.prisma.waliKelas.deleteMany({
        where: { id_sekolah, id_guru: guruId }
      });
      await this.prisma.waliKelas.upsert({
        where: { id_sekolah_id_kelas: { id_sekolah, id_kelas: kls.id } },
        update: { id_guru: guruId },
        create: { id_sekolah, id_kelas: kls.id, id_guru: guruId }
      });
    }

    return this.prisma.kelas.findUnique({
      where: { id: kls.id },
      include: { tingkatan: true, waliKelasList: { include: { guru: true } } }
    });
  }

  async update(id: number, id_sekolah: number, data: any) {
    const { id_guru_wali, ...kelasData } = data;

    if (kelasData.id_tingkatan) {
      kelasData.id_tingkatan = Number(kelasData.id_tingkatan);
    }

    if (Object.keys(kelasData).length > 0) {
      await this.prisma.kelas.updateMany({
        where: { id, id_sekolah },
        data: kelasData,
      });
    }

    if (id_guru_wali !== undefined) {
      if (id_guru_wali === null || id_guru_wali === '') {
        await this.prisma.waliKelas.deleteMany({
          where: { id_sekolah, id_kelas: id }
        });
      } else {
        const guruId = Number(id_guru_wali);
        await this.prisma.waliKelas.deleteMany({
          where: { id_sekolah, id_guru: guruId }
        });
        await this.prisma.waliKelas.upsert({
          where: { id_sekolah_id_kelas: { id_sekolah, id_kelas: id } },
          update: { id_guru: guruId },
          create: { id_sekolah, id_kelas: id, id_guru: guruId }
        });
      }
    }

    return this.prisma.kelas.findFirst({
      where: { id, id_sekolah },
      include: { tingkatan: true, waliKelasList: { include: { guru: true } } }
    });
  }

  async remove(id: number, id_sekolah: number) {
    return this.prisma.kelas.updateMany({
      where: { id, id_sekolah },
      data: { deleted_at: new Date() }
    });
  }
}

