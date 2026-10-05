import { CallHandler, ExecutionContext, Injectable, NestInterceptor } from '@nestjs/common';
import { Observable } from 'rxjs';

@Injectable()
export class TenantInterceptor implements NestInterceptor {
  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    const request = context.switchToHttp().getRequest();
    const user = request.user;
    
    // Inject id_sekolah into body, query, or params if user is authenticated
    if (user && user.id_sekolah) {
      if (request.method === 'POST' || request.method === 'PUT' || request.method === 'PATCH') {
        if (!request.body) request.body = {};
        request.body.id_sekolah = user.id_sekolah;
      } else if (request.method === 'GET') {
        if (!request.query) request.query = {};
        request.query.id_sekolah = user.id_sekolah;
      }
    }
    
    return next.handle();
  }
}
