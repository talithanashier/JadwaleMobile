import { Module } from '@nestjs/common';
import { KelasController } from './kelas.controller';
import { KelasService } from './kelas.service';

import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  controllers: [KelasController],
  providers: [KelasService]
})
export class KelasModule {}
