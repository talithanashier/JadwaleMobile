import { WebSocketGateway, WebSocketServer, SubscribeMessage, MessageBody, ConnectedSocket } from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';

@WebSocketGateway({
  cors: { origin: '*' },
})
export class JadwalGateway {
  @WebSocketServer()
  server: Server;

  // The client joins a room based on their id_sekolah to receive targeted updates
  @SubscribeMessage('joinRoom')
  handleJoinRoom(@MessageBody() data: { id_sekolah: number }, @ConnectedSocket() client: Socket) {
    client.join(`sekolah_${data.id_sekolah}`);
    return { event: 'joined', data: `sekolah_${data.id_sekolah}` };
  }

  sendProgress(id_sekolah: number, progress: number, message: string) {
    this.server.to(`sekolah_${id_sekolah}`).emit('progress', { progress, message });
  }

  sendResult(id_sekolah: number, status: 'success' | 'failed', message: string) {
    this.server.to(`sekolah_${id_sekolah}`).emit('result', { status, message });
  }
}
