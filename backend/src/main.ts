import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const allowedOrigins = process.env.ALLOWED_ORIGINS 
    ? process.env.ALLOWED_ORIGINS.split(',') 
    : ['http://localhost:3000', 'http://localhost:3001', 'http://127.0.0.1:3001'];
    
  app.enableCors({
    origin: process.env.NODE_ENV === 'production' ? allowedOrigins : true,
    credentials: true,
  });
  const port = process.env.PORT || 3001;
  await app.listen(port);
}
bootstrap();
