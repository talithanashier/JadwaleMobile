import { Controller, Post, Get, Query, Param, UseGuards, Request, Body, Res, UnauthorizedException, ForbiddenException } from '@nestjs/common';
import { JadwalService } from './jadwal.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { RequireAdmin } from '../auth/roles.decorator';
import type { Response } from 'express';
import { v4 as uuidv4 } from 'uuid';
import { PrismaService } from '../prisma/prisma.service';

import { ExportService } from '../export/export.service';

@Controller('api/jadwal')
export class JadwalController {
  constructor(
    private readonly jadwalService: JadwalService,
    private readonly exportService: ExportService,
    private readonly prisma: PrismaService,
  ) {}

  @Post('generate')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @RequireAdmin()
  generate(@Request() req: any) {
    return this.jadwalService.generateJadwalAsync(req.user.id_sekolah);
  }

  @Post('generate/preview')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @RequireAdmin()
  generatePreview(@Request() req: any, @Body('periodeId') periodeId?: number) {
    return this.jadwalService.generatePreview(req.user.id_sekolah, periodeId);
  }

  @Post('generate/commit')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @RequireAdmin()
  commitDraft(@Request() req: any, @Body('periodeId') periodeId?: number) {
    return this.jadwalService.commitDraft(req.user.id_sekolah, periodeId);
  }

  @Get('generate/status/:jobId')
  @UseGuards(JwtAuthGuard)
  getJobStatus(@Request() req: any, @Param('jobId') jobId: string) {
    return this.jadwalService.getJobStatus(jobId, req.user.id_sekolah);
  }

  @Get()
  @UseGuards(JwtAuthGuard)
  findAll(@Request() req: any, @Query('id_kelas') id_kelas?: string, @Query('id_periode') id_periode?: string) {
    return this.jadwalService.findAll(
      req.user.id_sekolah, 
      id_kelas ? +id_kelas : undefined,
      id_periode ? +id_periode : undefined
    );
  }

  @Get('my-schedule')
  @UseGuards(JwtAuthGuard)
  getMySchedule(@Request() req: any) {
    if (!req.user.id_guru) {
      throw new ForbiddenException('Fitur ini khusus untuk akun Tenaga Pendidik yang terikat data Guru.');
    }
    return this.jadwalService.findMyTeachingSchedule(req.user.id_guru, req.user.id_sekolah);
  }

  @Get('periode')
  @UseGuards(JwtAuthGuard)
  getPeriodes(@Request() req: any) {
    return this.jadwalService.getPeriodeList(req.user.id_sekolah);
  }

  @Post('periode')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @RequireAdmin()
  createPeriode(@Request() req: any, @Body() body: any) {
    return this.jadwalService.createPeriode(req.user.id_sekolah, body);
  }

  @Post('periode/:id/activate')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @RequireAdmin()
  setActivePeriode(@Request() req: any, @Param('id') id: string) {
    return this.jadwalService.setActivePeriode(req.user.id_sekolah, +id);
  }


  @Post('share')
  @UseGuards(JwtAuthGuard)
  async createShareLink(
    @Request() req: any, 
    @Body() body: { permission: string, days: number }
  ) {
    const uuid = uuidv4();
    const expiresAt = new Date();
    expiresAt.setDate(expiresAt.getDate() + (body.days || 30));
    const userId = req.user.id || req.user.userId;

    const link = await this.prisma.sharedLink.create({
      data: {
        id_sekolah: req.user.id_sekolah,
        uuid,
        permission: body.permission || 'read',
        created_by: userId,
        expires_at: expiresAt,
      }
    });

    return { uuid: link.uuid, url: `/share/${link.uuid}` };
  }

  @Get('share/:uuid')
  async getSharedJadwal(@Param('uuid') uuid: string, @Request() req: any) {
    const link = await this.prisma.sharedLink.findUnique({ where: { uuid } });
    if (!link) throw new UnauthorizedException('Link tidak ditemukan');
    
    if (link.expires_at && link.expires_at < new Date()) {
      throw new UnauthorizedException('Link sudah kadaluarsa');
    }

    // Increment view count
    await this.prisma.sharedLink.update({
      where: { uuid },
      data: { view_count: { increment: 1 } }
    });

    if (link.permission === 'edit') {
      const token = req.headers.authorization?.split(' ')[1];
      if (!token) throw new UnauthorizedException('Silakan login untuk mengedit jadwal');
    }

    return this.jadwalService.findAll(link.id_sekolah);
  }

  @Get('export/excel')
  @UseGuards(JwtAuthGuard)
  async exportExcel(@Request() req: any, @Res() res: Response, @Query('periodeId') periodeId?: string) {
    const pId = periodeId ? parseInt(periodeId, 10) : undefined;
    const buffer = await this.exportService.generateExcel(req.user.id_sekolah, pId);
    res.set({
      'Content-Type': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'Content-Disposition': 'attachment; filename="Jadwal_Pelajaran.xlsx"',
      'Content-Length': buffer.length.toString(),
    });
    res.end(buffer);
  }

  @Get('export/pdf')
  @UseGuards(JwtAuthGuard)
  async exportPdf(@Request() req: any, @Res() res: Response, @Query('periodeId') periodeId?: string) {
    const pId = periodeId ? parseInt(periodeId, 10) : undefined;
    const buffer = await this.exportService.generatePdf(req.user.id_sekolah, pId);
    res.set({
      'Content-Type': 'application/pdf',
      'Content-Disposition': 'attachment; filename="Jadwal_Pelajaran.pdf"',
      'Content-Length': buffer.length.toString(),
    });
    res.end(buffer);
  }
}
