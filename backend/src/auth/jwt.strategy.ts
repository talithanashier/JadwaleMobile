import { ExtractJwt, Strategy } from 'passport-jwt';
import { PassportStrategy } from '@nestjs/passport';
import { Injectable } from '@nestjs/common';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: process.env.JWT_SECRET || 'your_very_long_secret_key',
    });
  }

  async validate(payload: any) {
    const role = (payload.email === 'superadmin@jadwale.id' || payload.role === 'SUPER_ADMIN')
      ? 'SUPER_ADMIN'
      : payload.role || (payload.is_admin ? 'ADMIN_SEKOLAH' : 'TENAGA_PENDIDIK');

    return { 
      id: payload.sub,
      userId: payload.sub, 
      email: payload.email, 
      id_sekolah: payload.id_sekolah, 
      id_guru: payload.id_guru,
      role,
      is_admin: payload.is_admin 
    };
  }
}
