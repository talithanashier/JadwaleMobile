import { Module } from '@nestjs/common';
import { GuruController } from './guru.controller';
import { GuruService } from './guru.service';

import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  controllers: [GuruController],
  providers: [GuruService]
})
export class GuruModule {}
