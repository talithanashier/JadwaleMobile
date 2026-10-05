import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Get('api')
  getApiStatus() {
    return {
      status: 'ok',
      name: 'Jadwale API Service',
      version: '3.1 Standar Kemendikbudristek',
      timestamp: new Date().toISOString()
    };
  }

  @Get('api/notifications')
  getNotifications() {
    return [
      {
        id: 1,
        title: "✓ Jadwal Siap Digunakan",
        message: "Semester Ganjil 2025/2026 SDN Pancasila 01 telah 100% teralokasi bebas bentrok (480 JP).",
        type: "success",
        date: new Date().toISOString()
      },
      {
        id: 2,
        title: "🏛️ Validasi Kemdikbudristek",
        message: "Sesuai Kurikulum Merdeka Fase A, B, dan C (24 Guru Terdaftar).",
        type: "info",
        date: new Date().toISOString()
      },
      {
        id: 3,
        title: "Pembaruan Sistem",
        message: "Fitur Ekspor PDF dan Import Excel telah diperbarui untuk mendukung format terbaru.",
        type: "warning",
        date: new Date().toISOString()
      }
    ];
  }
}

