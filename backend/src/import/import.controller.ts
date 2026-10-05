import {
  Controller,
  Post,
  Get,
  Param,
  Query,
  Body,
  UseGuards,
  UseInterceptors,
  UploadedFile,
  Request,
  Res,
  BadRequestException,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { memoryStorage } from 'multer';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { ImportService, UploadedFileType } from './import.service';
import { TemplateService } from './template/template.service';
import { ImportQueryDto } from './dto/import-query.dto';
import type { Response } from 'express';

const uploadOptions = {
  storage: memoryStorage(),
  limits: { fileSize: 5 * 1024 * 1024 }, // 5 MB
};



@Controller('api/import')
export class ImportController {
  constructor(
    private readonly importService: ImportService,
    private readonly templateService: TemplateService,
  ) {}

  // ─── Download Template (WITH AUTH - preferred) ─────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Get('template/:entitas')
  async downloadTemplate(
    @Param('entitas') entitas: string,
    @Request() req: any,
    @Res() res: Response,
  ) {
    const sekolahId = req?.user?.id_sekolah ?? undefined;
    const buffer = await this.templateService.generateTemplate(entitas, sekolahId);
    const filename = `template-import-${entitas}.xlsx`;

    res.set({
      'Content-Type': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'Content-Disposition': `attachment; filename="${filename}"`,
      'Content-Length': buffer.length.toString(),
      'Cache-Control': 'no-cache',
    });
    res.end(buffer);
  }

  // ─── Import Guru ───────────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('guru')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async importGuru(
    @UploadedFile() file: UploadedFileType,
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File Excel/CSV wajib diupload');
    return this.importService.previewEntity(file, req.user.id_sekolah, 'guru', query.mode);
  }

  // ─── Import Kelas ──────────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('kelas')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async importKelas(
    @UploadedFile() file: UploadedFileType,
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File Excel/CSV wajib diupload');
    return this.importService.previewEntity(file, req.user.id_sekolah, 'kelas', query.mode);
  }

  // ─── Import Mapel ──────────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('mapel')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async importMapel(
    @UploadedFile() file: UploadedFileType,
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File Excel/CSV wajib diupload');
    return this.importService.previewEntity(file, req.user.id_sekolah, 'mapel', query.mode);
  }

  // ─── Import Siswa ──────────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('siswa')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async importSiswa(
    @UploadedFile() file: UploadedFileType,
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File Excel/CSV wajib diupload');
    return this.importService.previewEntity(file, req.user.id_sekolah, 'siswa', query.mode);
  }

  // ─── Import Jadwal ─────────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('jadwal')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async importJadwal(
    @UploadedFile() file: UploadedFileType,
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File Excel/CSV wajib diupload');
    return this.importService.previewEntity(file, req.user.id_sekolah, 'jadwal', query.mode);
  }

  // ─── Import All (multi-sheet) ──────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('all')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async importAll(
    @UploadedFile() file: UploadedFileType,
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File Excel multi-sheet wajib diupload');
    return this.importService.previewAll(file, req.user.id_sekolah, query.mode);
  }

  // ─── Preview General ───────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('preview')
  @UseInterceptors(FileInterceptor('file', uploadOptions))
  async previewGeneral(
    @UploadedFile() file: UploadedFileType,
    @Query('entitas') entitas: string = 'guru',
    @Query() query: ImportQueryDto,
    @Request() req: any,
  ) {
    if (!file) throw new BadRequestException('File wajib diupload');
    return this.importService.previewEntity(file, req.user.id_sekolah, entitas as any, query.mode);
  }

  // ─── Commit Import ─────────────────────────────────────────────────────────
  @UseGuards(JwtAuthGuard)
  @Post('commit')
  async commitImport(
    @Body('previewToken') previewToken?: string,
    @Body('previewTokens') previewTokens?: string[],
    @Request() req?: any,
  ) {
    const tokens =
      previewTokens && previewTokens.length > 0
        ? previewTokens
        : previewToken
        ? [previewToken]
        : [];
    if (tokens.length === 0)
      throw new BadRequestException('previewToken atau previewTokens wajib diisi dalam request body');
    return this.importService.commit(tokens, req?.user?.id_sekolah);
  }
}
