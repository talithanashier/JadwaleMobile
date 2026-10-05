import { Controller, Post, Body, UnauthorizedException, Request, UseGuards, Get, Put } from '@nestjs/common';
import { AuthService } from './auth.service';
import { JwtAuthGuard } from './jwt-auth.guard';
import { PrismaService } from '../prisma/prisma.service';
import {
  LoginDto,
  SignupSchoolDto,
  SignupTeacherDto,
  SignupPublicDto,
  UpdateProfileDto,
  ForgotPasswordDto,
  ResetPasswordDto,
} from './dto/auth.dto';

@Controller('api/auth')
export class AuthController {
  constructor(
    private authService: AuthService,
    private prisma: PrismaService
  ) {}

  @Post('login')
  async login(@Body() body: LoginDto) {
    const user = await this.authService.validateUser(body.email, body.password);
    if (!user) {
      throw new UnauthorizedException('Kredensial tidak valid');
    }
    return this.authService.login(user);
  }

  // Pendaftaran Sekolah (Admin Sekolah)
  @Post('signup')
  async signup(@Body() body: SignupSchoolDto) {
    return this.authService.registerSchool(body);
  }

  @Post('signup/school')
  async signupSchool(@Body() body: SignupSchoolDto) {
    return this.authService.registerSchool(body);
  }

  // Pendaftaran Tenaga Pendidik (Guru)
  @Post('signup/teacher')
  async signupTeacher(@Body() body: SignupTeacherDto) {
    return this.authService.registerTeacher(body);
  }

  // Pendaftaran User Biasa (Wali Murid / Umum)
  @Post('signup/public')
  async signupPublic(@Body() body: SignupPublicDto) {
    return this.authService.registerPublicUser(body);
  }

  // Public endpoint untuk opsi pilihan Sekolah saat Guru mendaftar (Hanya sekolah terverifikasi)
  @Get('sekolah-list')
  async getPublicSekolahList() {
    return this.prisma.sekolah.findMany({
      where: {
        deleted_at: null,
        users: {
          some: {
            role: 'ADMIN_SEKOLAH',
            is_verified: true,
          }
        }
      },
      select: {
        id: true,
        nama_sekolah: true,
        npsn: true,
        alamat: true,
      },
      orderBy: { nama_sekolah: 'asc' }
    });
  }

  @Get('me')
  @UseGuards(JwtAuthGuard)
  getProfile(@Request() req: any) {
    return this.authService.getMe(req.user.userId);
  }

  @Put('profile')
  @UseGuards(JwtAuthGuard)
  async updateProfile(@Request() req: any, @Body() body: UpdateProfileDto) {
    return this.authService.updateProfile(req.user.userId, body);
  }

  @Post('forgot-password')
  async forgotPassword(@Body() body: ForgotPasswordDto) {
    return this.authService.forgotPassword(body.email);
  }

  @Post('reset-password')
  async resetPassword(@Body() body: ResetPasswordDto) {
    const password = body.new_password || body.password || '';
    return this.authService.resetPassword(body.token, password);
  }
}
