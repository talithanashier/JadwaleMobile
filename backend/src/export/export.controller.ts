import { Controller, Get, Query, Res, UseGuards, Request } from '@nestjs/common';
import { ExportService } from './export.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { ExportQueryDto } from './dto/export-query.dto';
import type { Response } from 'express';

@UseGuards(JwtAuthGuard)
@Controller(['api/export', 'api/jadwal/export'])
export class ExportController {
  constructor(private readonly exportService: ExportService) {}

  @Get('excel')
  async exportExcel(@Request() req: any, @Query() query: ExportQueryDto, @Res() res: Response) {
    const idSekolah = req.user.id_sekolah;
    const periodeId = query.periodeId ? parseInt(query.periodeId, 10) : undefined;
    
    const buffer = await this.exportService.generateExcel(idSekolah, periodeId);

    const filename = `jadwal-pelajaran-${Date.now()}.xlsx`;

    res.set({
      'Content-Type': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'Content-Disposition': `attachment; filename="${filename}"`,
      'Content-Length': buffer.length.toString(),
    });

    res.end(buffer);
  }

  @Get('pdf')
  async exportPdf(@Request() req: any, @Query() query: ExportQueryDto, @Res() res: Response) {
    const idSekolah = req.user.id_sekolah;
    const periodeId = query.periodeId ? parseInt(query.periodeId, 10) : undefined;

    const buffer = await this.exportService.generatePdf(idSekolah, periodeId);

    const filename = `jadwal-pelajaran-${Date.now()}.pdf`;

    res.set({
      'Content-Type': 'application/pdf',
      'Content-Disposition': `attachment; filename="${filename}"`,
      'Content-Length': buffer.length.toString(),
    });

    res.end(buffer);
  }
}
