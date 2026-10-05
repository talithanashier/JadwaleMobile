import { Controller, Get, Post, Body, Patch, Put, Param, Delete, UseGuards, Request } from '@nestjs/common';
import { KelasService } from './kelas.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@UseGuards(JwtAuthGuard)
@Controller('api/kelas')
export class KelasController {
  constructor(private readonly kelasService: KelasService) {}

  @Post()
  create(@Request() req: any, @Body() createKelasDto: any) {
    return this.kelasService.create(req.user.id_sekolah, createKelasDto);
  }

  @Get()
  findAll(@Request() req: any) {
    return this.kelasService.findAll(req.user.id_sekolah);
  }

  @Patch(':id')
  update(@Request() req: any, @Param('id') id: string, @Body() updateKelasDto: any) {
    return this.kelasService.update(+id, req.user.id_sekolah, updateKelasDto);
  }

  @Put(':id')
  updatePut(@Request() req: any, @Param('id') id: string, @Body() updateKelasDto: any) {
    return this.kelasService.update(+id, req.user.id_sekolah, updateKelasDto);
  }

  @Delete(':id')
  remove(@Request() req: any, @Param('id') id: string) {
    return this.kelasService.remove(+id, req.user.id_sekolah);
  }
}

