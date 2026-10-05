import { SetMetadata } from '@nestjs/common';

export const IS_ADMIN_KEY = 'is_admin';
export const ROLES_KEY = 'roles';

export const RequireAdmin = () => SetMetadata(IS_ADMIN_KEY, true);
export const Roles = (...roles: string[]) => SetMetadata(ROLES_KEY, roles);

