import { Controller, Get, Post, Body, Patch, UseGuards, Request } from '@nestjs/common';
import { SekolahService } from './sekolah.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@UseGuards(JwtAuthGuard)
@Controller('api/sekolah')
export class SekolahController {
  constructor(private readonly sekolahService: SekolahService) {}

  @Get('stats')
  getStats(@Request() req: any) {
    return this.sekolahService.getStats(req.user.id_sekolah);
  }

  @Get()
  getProfile(@Request() req: any) {
    return this.sekolahService.getProfile(req.user.id_sekolah);
  }

  @Patch()
  updateProfile(@Request() req: any, @Body() updateData: any) {
    return this.sekolahService.updateProfile(req.user.id_sekolah, updateData);
  }

  @Post('hydrate')
  async hydrate(@Request() req: any, @Body() hydrateData: any) {
    return this.sekolahService.hydrate(req.user.id_sekolah, hydrateData);
  }
}
