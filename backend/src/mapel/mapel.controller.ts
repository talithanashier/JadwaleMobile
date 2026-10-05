import { Controller, Get, Post, Body, Patch, Put, Param, Delete, UseGuards, Request } from '@nestjs/common';
import { MapelService } from './mapel.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@UseGuards(JwtAuthGuard)
@Controller('api/mapel')
export class MapelController {
  constructor(private readonly mapelService: MapelService) {}

  @Post()
  create(@Request() req: any, @Body() createMapelDto: any) {
    return this.mapelService.create(req.user.id_sekolah, createMapelDto);
  }

  @Get()
  findAll(@Request() req: any) {
    return this.mapelService.findAll(req.user.id_sekolah);
  }

  @Patch(':id')
  update(@Request() req: any, @Param('id') id: string, @Body() updateMapelDto: any) {
    return this.mapelService.update(+id, req.user.id_sekolah, updateMapelDto);
  }

  @Put(':id')
  updatePut(@Request() req: any, @Param('id') id: string, @Body() updateMapelDto: any) {
    return this.mapelService.update(+id, req.user.id_sekolah, updateMapelDto);
  }

  @Delete(':id')
  remove(@Request() req: any, @Param('id') id: string) {
    return this.mapelService.remove(+id, req.user.id_sekolah);
  }
}

