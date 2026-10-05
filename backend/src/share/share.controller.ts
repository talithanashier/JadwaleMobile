import { Controller, Get, Param, Post, Body, UseGuards, Request, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { ShareService } from './share.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('api/share')
export class ShareController {
  constructor(private readonly shareService: ShareService) {}

  @UseGuards(JwtAuthGuard)
  @Post()
  async createLink(@Request() req: any, @Body() body: { id_kelas?: number, permission?: string, allowed_user_id?: number, expires_in_days?: number }) {
    return this.shareService.createLink({
      id_sekolah: req.user.id_sekolah,
      created_by: req.user.id,
      id_kelas: body.id_kelas,
      permission: body.permission,
      allowed_user_id: body.allowed_user_id,
      expires_in_days: body.expires_in_days
    });
  }

  // Public endpoint for shared links
  @Get(':uuid')
  async getSharedData(@Param('uuid') uuid: string) {
    return this.shareService.getSharedData(uuid);
  }
}
