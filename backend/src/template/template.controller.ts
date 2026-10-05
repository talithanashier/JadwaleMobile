import { Controller, Get, Post, Body, UseGuards, Request } from '@nestjs/common';
import { TemplateService } from './template.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { RequireAdmin } from '../auth/roles.decorator';

@Controller('api/templates')
export class TemplateController {
  constructor(private readonly templateService: TemplateService) {}

  @Get()
  async findAll() {
    return this.templateService.findAll();
  }

  @UseGuards(JwtAuthGuard, RolesGuard)
  @RequireAdmin()
  @Post()
  async create(@Request() req: any, @Body() body: any) {
    return this.templateService.create({
      name: body.name,
      thumbnail: body.thumbnail,
      css_styles: body.css_styles,
      is_premium: body.is_premium,
      created_by: req.user.id
    });
  }
}
