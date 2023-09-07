import { RequestListener, createServer } from 'http';
import { Server, Socket } from 'socket.io';

import SocketPool from './socket-pool';

export default class Gateway {
  private io: Server;
  private pool: SocketPool;

  constructor() {
    this.io = new Server();
    this.pool = new SocketPool();

    this.io.on('connection', this.setupSocket.bind(this));
  }

  private handleError(socket: Socket, e: Error) {
    const type = e.message.split(':')[0];
    const error = e.message.split(':')[1] || type;
    socket.emit('error', {
      type,
      message: error.trim(),
    });
  }

  private handleAuth(socket: Socket, token: string) {
    this.pool
      .addSocket(socket, token)
      .catch((e) => this.handleError(socket, e));
  }

  private handleDisconnection(socket: Socket) {
    this.pool.disconnectSocket(socket);
  }

  private setupSocket(socket: Socket) {
    socket.on('auth', (token: string) => {
      this.handleAuth(socket, token);
    });

    socket.on('disconnect', () => {
      this.handleDisconnection(socket);
    });
  }

  public debug(widgetId: string, eventName: string, eventData: any) {
    const sockets = this.pool.getSocketsByWidget(widgetId);
    sockets.forEach((socket) => {
      socket.emit(eventName, eventData);
    });
  }

  public createServer(handler: RequestListener) {
    const server = createServer(handler);
    this.io.attach(server);
    return server;
  }
}
