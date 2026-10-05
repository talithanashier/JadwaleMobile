import { Controller, Get, Put, Post, Param, Body, UseGuards } from '@nestjs/common';
import { AdminService } from './admin.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { Roles } from '../auth/roles.decorator';

@UseGuards(JwtAuthGuard, RolesGuard)
@Roles('SUPER_ADMIN')
@Controller('api/admin')
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Get('users')
  async getUsers() {
    return this.adminService.getUsers();
  }

  @Get('schools')
  async getSekolahList() {
    return this.adminService.getSekolahList();
  }

  @Put('users/:id/status')
  async setStatus(@Param('id') id: string, @Body('is_active') is_active: boolean) {
    return this.adminService.setStatus(+id, is_active);
  }

  @Get('pending-schools')
  async getPendingSchools() {
    return this.adminService.getPendingSchools();
  }

  @Post('verify-school/:id')
  async verifySchool(@Param('id') id: string) {
    return this.adminService.verifySchool(+id);
  }

  @Post('reject-school/:id')
  async rejectSchool(@Param('id') id: string) {
    return this.adminService.rejectSchool(+id);
  }

  @Post('designers')
  async createDesigner(@Body() body: { nama: string; email: string; password: string }) {
    return this.adminService.createDesigner(body);
  }

  @Get('stats')
  async getStats() {
    return this.adminService.getStats();
  }

  @Get('export-backup')
  async exportBackup() {
    return this.adminService.exportBackup();
  }
}

