import { Module } from '@nestjs/common';
import { ImportController } from './import.controller';
import { ImportService } from './import.service';
import { TemplateService } from './template/template.service';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  controllers: [ImportController],
  providers: [ImportService, TemplateService],
  exports: [ImportService, TemplateService],
})
export class ImportModule {}
