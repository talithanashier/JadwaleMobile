import { Controller, Get, Post, Body, Patch, Put, Param, Delete, UseGuards, Request } from '@nestjs/common';
import { GuruService } from './guru.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@UseGuards(JwtAuthGuard)
@Controller('api/guru')
export class GuruController {
  constructor(private readonly guruService: GuruService) {}

  @Post()
  create(@Request() req: any, @Body() createGuruDto: any) {
    return this.guruService.create(req.user.id_sekolah, createGuruDto);
  }

  @Get()
  findAll(@Request() req: any) {
    return this.guruService.findAll(req.user.id_sekolah);
  }

  @Get('pending/list')
  getPendingTeachers(@Request() req: any) {
    return this.guruService.getPendingTeachers(req.user.id_sekolah);
  }

  @Get(':id')
  findOne(@Request() req: any, @Param('id') id: string) {
    return this.guruService.findOne(+id, req.user.id_sekolah);
  }

  @Patch(':id')
  update(@Request() req: any, @Param('id') id: string, @Body() updateGuruDto: any) {
    return this.guruService.update(+id, req.user.id_sekolah, updateGuruDto);
  }

  @Put(':id')
  updatePut(@Request() req: any, @Param('id') id: string, @Body() updateGuruDto: any) {
    return this.guruService.update(+id, req.user.id_sekolah, updateGuruDto);
  }

  @Delete(':id')
  remove(@Request() req: any, @Param('id') id: string) {
    return this.guruService.remove(+id, req.user.id_sekolah);
  }

  @Post(':id/verify')
  verifyTeacher(@Request() req: any, @Param('id') id: string) {
    return this.guruService.verifyTeacher(+id, req.user.id_sekolah);
  }

  @Post(':id/reject')
  rejectTeacher(@Request() req: any, @Param('id') id: string) {
    return this.guruService.rejectTeacher(+id, req.user.id_sekolah);
  }

  @Post(':id/availability')
  setAvailability(@Request() req: any, @Param('id') id: string, @Body() body: any) {
    return this.guruService.setAvailability(+id, req.user.id_sekolah, body.availabilities);
  }
}


