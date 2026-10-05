import { Injectable, UnauthorizedException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class AuthService {
  constructor(
    private prisma: PrismaService,
    private jwtService: JwtService
  ) {}

  async validateUser(email: string, pass: string): Promise<any> {
    if (!email || !pass) {
      throw new UnauthorizedException('Email dan password harus diisi');
    }
    const user = await this.prisma.user.findUnique({ 
      where: { email },
      include: { sekolah: true, guru: true }
    });

    if (!user) {
      throw new UnauthorizedException('Email atau password salah');
    }

    const isMatch = await bcrypt.compare(pass, user.password);
    if (!isMatch) {
      throw new UnauthorizedException('Email atau password salah');
    }

    if (user.is_active === false) {
      throw new UnauthorizedException('Akun Anda telah dinonaktifkan oleh administrator.');
    }

    if (user.is_verified === false) {
      if (user.role === 'ADMIN_SEKOLAH') {
        throw new UnauthorizedException('Akun Admin Sekolah Anda sedang menunggu verifikasi dari Superadmin.');
      } else if (user.role === 'TENAGA_PENDIDIK') {
        const sekolahNama = user.sekolah?.nama_sekolah ? ` (${user.sekolah.nama_sekolah})` : '';
        throw new UnauthorizedException(`Akun Tenaga Pendidik Anda sedang menunggu verifikasi dari Admin Sekolah${sekolahNama}.`);
      } else {
        throw new UnauthorizedException('Akun Anda belum diverifikasi.');
      }
    }

    const { password, ...result } = user;
    return result;
  }

  async login(user: any) {
    const payload = { 
      email: user.email, 
      sub: user.id, 
      id_sekolah: user.id_sekolah,
      id_guru: user.id_guru,
      role: (user.email === 'superadmin@jadwale.id' || user.role === 'SUPER_ADMIN')
        ? 'SUPER_ADMIN'
        : user.role || (user.is_admin ? 'ADMIN_SEKOLAH' : 'TENAGA_PENDIDIK'),
      is_admin: user.is_admin || user.role === 'ADMIN_SEKOLAH' || user.role === 'SUPER_ADMIN',
      is_verified: user.is_verified
    };

    return {
      access_token: this.jwtService.sign(payload),
      user: {
        id: user.id,
        nama: user.nama,
        email: user.email,
        role: payload.role,
        is_admin: payload.is_admin,
        id_sekolah: user.id_sekolah,
        id_guru: user.id_guru,
        is_verified: user.is_verified,
        sekolah: user.sekolah
      }
    };
  }

  async getMe(userId: number) {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      include: { sekolah: true, guru: true },
    });
    if (!user) {
      throw new UnauthorizedException('User tidak ditemukan');
    }
    const { password, ...result } = user;
    const role = (user.email === 'superadmin@jadwale.id' || user.role === 'SUPER_ADMIN')
      ? 'SUPER_ADMIN'
      : user.role || (user.is_admin ? 'ADMIN_SEKOLAH' : 'TENAGA_PENDIDIK');

    return {
      ...result,
      role,
      is_admin: user.is_admin || role === 'ADMIN_SEKOLAH' || role === 'SUPER_ADMIN',
    };
  }

  async updateProfile(userId: number, data: { nama?: string; email?: string; no_hp?: string; password?: string }) {
    const updateData: any = {};
    if (data.nama) updateData.nama = data.nama.trim();
    if (data.email) updateData.email = data.email.trim().toLowerCase();
    if (data.no_hp) updateData.no_hp = data.no_hp.trim();
    if (data.password && data.password.length >= 6) {
      updateData.password = await bcrypt.hash(data.password, 10);
    }

    const updatedUser = await this.prisma.user.update({
      where: { id: userId },
      data: updateData,
      include: { sekolah: true, guru: true },
    });

    const { password, ...result } = updatedUser;
    return result;
  }

  // 1. Pendaftaran Sekolah (Admin Sekolah) - Memerlukan Verifikasi Superadmin
  async registerSchool(data: { nama: string; email: string; password: string; nama_sekolah: string; npsn?: string }) {
    const cleanEmail = data.email.trim().toLowerCase();
    if (!cleanEmail.includes('sch.id')) {
      throw new BadRequestException('Email pendaftaran sekolah wajib menggunakan domain resmi sekolah (*.sch.id).');
    }

    const existingUser = await this.prisma.user.findUnique({ where: { email: cleanEmail } });
    if (existingUser) {
      throw new BadRequestException('Email sudah terdaftar. Silakan gunakan email lain.');
    }

    const npsn = data.npsn || String(Math.floor(10000000 + Math.random() * 90000000));
    const hashedPassword = await bcrypt.hash(data.password, 10);

    return this.prisma.$transaction(async (tx) => {
      const sekolah = await tx.sekolah.create({
        data: {
          nama_sekolah: data.nama_sekolah.trim(),
          npsn,
          status: 'PENDING_VERIFIKASI',
          setupCompleted: false,
          config: {
            create: {
              is_parallel: false,
              class_naming: 'alphabet',
              school_days: 5,
              start_time: new Date('1970-01-01T07:00:00Z'),
              duration_per_jp: 35,
              has_routine: true,
              routine_duration: 15,
              has_monday_ceremony: true,
            }
          }
        }
      });

      const user = await tx.user.create({
        data: {
          nama: data.nama.trim(),
          email: cleanEmail,
          password: hashedPassword,
          role: 'ADMIN_SEKOLAH',
          is_admin: true,
          is_verified: false, // Perlu verifikasi Super Admin
          id_sekolah: sekolah.id,
        }
      });

      return {
        message: 'Pendaftaran sekolah berhasil. Akun Admin Sekolah Anda sedang menunggu verifikasi oleh Super Admin.',
        requires_verification: true,
        user: { id: user.id, nama: user.nama, email: user.email, role: user.role, is_verified: false }
      };
    });
  }

  // 2. Pendaftaran Tenaga Pendidik (Guru) - Memerlukan Verifikasi Admin Sekolah
  async registerTeacher(data: { nama: string; email: string; password: string; id_sekolah: number; nip?: string }) {
    const cleanEmail = data.email.trim().toLowerCase();
    if (!cleanEmail.includes('guru.sd.belajar.id') && !cleanEmail.includes('belajar.id')) {
      throw new BadRequestException('Email pendaftaran guru wajib menggunakan domain belajar (*.guru.sd.belajar.id atau *.belajar.id).');
    }

    const existingUser = await this.prisma.user.findUnique({ where: { email: cleanEmail } });
    if (existingUser) {
      throw new BadRequestException('Email sudah terdaftar. Silakan gunakan email lain.');
    }

    const sekolah = await this.prisma.sekolah.findUnique({
      where: { id: Number(data.id_sekolah) },
      include: { users: { where: { role: 'ADMIN_SEKOLAH', is_verified: true } } }
    });
    if (!sekolah) {
      throw new BadRequestException('Sekolah yang dipilih tidak ditemukan.');
    }

    const hashedPassword = await bcrypt.hash(data.password, 10);

    return this.prisma.$transaction(async (tx) => {
      let guru = null;
      if (data.nip) {
        guru = await tx.guru.findUnique({ where: { nip: data.nip.trim() } });
      }

      if (!guru) {
        guru = await tx.guru.create({
          data: {
            id_sekolah: sekolah.id,
            nama: data.nama.trim(),
            nip: data.nip ? data.nip.trim() : null,
          }
        });
      }

      const user = await tx.user.create({
        data: {
          nama: data.nama.trim(),
          email: cleanEmail,
          password: hashedPassword,
          role: 'TENAGA_PENDIDIK',
          is_admin: false,
          is_verified: false, // Perlu verifikasi Admin Sekolah
          id_sekolah: sekolah.id,
          id_guru: guru.id,
        }
      });

      return {
        message: `Pendaftaran Tenaga Pendidik berhasil. Akun Anda sedang menunggu persetujuan dari Admin Sekolah (${sekolah.nama_sekolah}).`,
        requires_verification: true,
        user: { id: user.id, nama: user.nama, email: user.email, role: user.role, is_verified: false }
      };
    });
  }

  // 3. Pendaftaran User Biasa (Umum) - Dinonaktifkan sesuai spesifikasi baru
  async registerPublicUser(data: { nama: string; email: string; password: string }) {
    throw new BadRequestException('Pendaftaran akun tamu/umum telah ditiadakan. Sistem pendaftaran hanya tersedia untuk Akun Sekolah (*.sch.id) dan Guru (*.guru.sd.belajar.id).');
  }

  async forgotPassword(email: string) {
    if (!email) {
      throw new BadRequestException('Email wajib diisi.');
    }

    const cleanEmail = email.trim().toLowerCase();
    const user = await this.prisma.user.findUnique({
      where: { email: cleanEmail }
    });

    if (!user) {
      return {
        success: true,
        message: 'Jika email Anda terdaftar, petunjuk reset password telah dikirimkan.'
      };
    }

    const resetToken = uuidv4();
    const expiresAt = new Date(Date.now() + 30 * 60 * 1000); // 30 minutes

    await this.prisma.user.update({
      where: { id: user.id },
      data: {
        reset_token: resetToken,
        reset_token_expires: expiresAt,
      }
    });

    return {
      success: true,
      message: 'Petunjuk reset password telah dibuat.',
      resetToken,
      resetUrl: `/reset-password?token=${resetToken}`
    };
  }

  async resetPassword(token: string, newPassword: string) {
    if (!token || !newPassword) {
      throw new BadRequestException('Token dan password baru wajib diisi.');
    }

    if (newPassword.length < 6) {
      throw new BadRequestException('Password minimal harus 6 karakter.');
    }

    const user = await this.prisma.user.findFirst({
      where: {
        reset_token: token,
        reset_token_expires: { gte: new Date() }
      }
    });

    if (!user) {
      throw new BadRequestException('Tautan reset password tidak valid atau telah kedaluwarsa.');
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);

    await this.prisma.user.update({
      where: { id: user.id },
      data: {
        password: hashedPassword,
        reset_token: null,
        reset_token_expires: null,
      }
    });

    return {
      success: true,
      message: 'Password Anda berhasil diperbarui. Silakan login dengan password baru.'
    };
  }
}

