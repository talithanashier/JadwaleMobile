import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class TemplateService {
  constructor(private prisma: PrismaService) {}

  async findAll() {
    return this.prisma.template.findMany();
  }

  async create(data: { name: string, thumbnail?: string, css_styles: string, is_premium?: boolean, created_by: number }) {
    return this.prisma.template.create({ data });
  }
}
