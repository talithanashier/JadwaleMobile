import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class MapelService {
  constructor(private prisma: PrismaService) {}

  async findAll(id_sekolah: number) {
    return this.prisma.mapel.findMany({
      where: { id_sekolah, deleted_at: null },
      include: {
        mapelTingkatans: {
          include: { tingkatan: true }
        }
      },
      orderBy: { id: 'asc' }
    });
  }

  async create(id_sekolah: number, data: any) {
    const { jp_per_tingkatan, ...mapelData } = data;
    
    if (mapelData.prioritas !== undefined) {
      mapelData.prioritas = Boolean(mapelData.prioritas);
    }

    const mapel = await this.prisma.mapel.create({
      data: {
        ...mapelData,
        id_sekolah,
      }
    });

    if (jp_per_tingkatan && typeof jp_per_tingkatan === 'object') {
      const tingkatans = await this.prisma.tingkatan.findMany({
        where: { id_sekolah, deleted_at: null }
      });

      for (const tingkatan of tingkatans) {
        const tNum = parseInt(tingkatan.nama.replace(/\D/g, '')) || tingkatan.id;
        const jp = Number(jp_per_tingkatan[tNum] || jp_per_tingkatan[tingkatan.id] || 0);
        if (jp > 0) {
          await this.prisma.mapelTingkatan.create({
            data: {
              id_sekolah,
              id_mapel: mapel.id,
              id_tingkatan: tingkatan.id,
              jp_per_minggu: jp
            }
          });
        }
      }
    }

    return this.prisma.mapel.findUnique({
      where: { id: mapel.id },
      include: { mapelTingkatans: { include: { tingkatan: true } } }
    });
  }

  async update(id: number, id_sekolah: number, data: any) {
    const { jp_per_tingkatan, ...mapelData } = data;

    if (mapelData.prioritas !== undefined) {
      mapelData.prioritas = Boolean(mapelData.prioritas);
    }

    if (Object.keys(mapelData).length > 0) {
      await this.prisma.mapel.updateMany({
        where: { id, id_sekolah },
        data: mapelData,
      });
    }

    if (jp_per_tingkatan && typeof jp_per_tingkatan === 'object') {
      const tingkatans = await this.prisma.tingkatan.findMany({
        where: { id_sekolah, deleted_at: null }
      });

      for (const tingkatan of tingkatans) {
        const tNum = parseInt(tingkatan.nama.replace(/\D/g, '')) || tingkatan.id;
        const jp = Number(jp_per_tingkatan[tNum] || jp_per_tingkatan[tingkatan.id] || 0);
        
        await this.prisma.mapelTingkatan.deleteMany({
          where: { id_sekolah, id_mapel: id, id_tingkatan: tingkatan.id }
        });

        if (jp > 0) {
          await this.prisma.mapelTingkatan.create({
            data: {
              id_sekolah,
              id_mapel: id,
              id_tingkatan: tingkatan.id,
              jp_per_minggu: jp
            }
          });
        }
      }
    }

    return this.prisma.mapel.findFirst({
      where: { id, id_sekolah },
      include: { mapelTingkatans: { include: { tingkatan: true } } }
    });
  }

  async remove(id: number, id_sekolah: number) {
    return this.prisma.mapel.updateMany({
      where: { id, id_sekolah },
      data: { deleted_at: new Date() }
    });
  }
}

