import { Injectable, CanActivate, ExecutionContext } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { IS_ADMIN_KEY, ROLES_KEY } from './roles.decorator';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const requiredRoles = this.reflector.getAllAndOverride<string[]>(ROLES_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    const requireAdmin = this.reflector.getAllAndOverride<boolean>(IS_ADMIN_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    const { user } = context.switchToHttp().getRequest();
    if (!user) return false;

    if (user.email === 'superadmin@jadwale.id' || user.role === 'SUPER_ADMIN') {
      return true;
    }

    if (requiredRoles && requiredRoles.length > 0) {
      const userRole = user.role || (user.is_admin ? 'ADMIN_SEKOLAH' : 'TENAGA_PENDIDIK');
      return requiredRoles.includes(userRole);
    }

    if (requireAdmin) {
      return user.is_admin === true || user.role === 'ADMIN_SEKOLAH';
    }

    return true;
  }
}

