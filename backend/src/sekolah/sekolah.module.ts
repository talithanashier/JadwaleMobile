import { Module } from '@nestjs/common';
import { SekolahController } from './sekolah.controller';
import { SekolahService } from './sekolah.service';

import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  controllers: [SekolahController],
  providers: [SekolahService]
})
export class SekolahModule {}
