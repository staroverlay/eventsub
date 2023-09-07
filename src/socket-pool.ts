import { Socket } from 'socket.io';
import { getWidgetByToken } from './services/widgetService';
import { getIntegrationByWidget } from './services/integrationService';
import { Integration } from './schemas/integration';
import Subscriber from './interfaces/subscriber';
import { createTwitchSubscriber } from './subscribers/twitch';

export default class SocketPool {
  private subscribers: Map<string, Subscriber> = new Map();
  private sockets: Map<string, Set<Socket>> = new Map();

  constructor() {
    this.subscribers = new Map();
    this.sockets = new Map();
  }

  private async createSubscriber(integration: Integration) {
    let subscriber: Subscriber | null = null;

    if (integration.type == 'twitch') {
      subscriber = await createTwitchSubscriber(
        integration.integrationId,
        integration.refreshToken,
      );
    }

    return subscriber;
  }

  public async addSocket(socket: Socket, token: string) {
    const widget = await getWidgetByToken(token);
    if (!widget) {
      throw new Error('BAD_AUTH: Widget with this token not found');
    }

    const integration = await getIntegrationByWidget(widget);
    if (!integration) {
      throw new Error('BAD_AUTH: Integration for this widget not found');
    }

    const subscriber: Subscriber = await this.createSubscriber(
      integration,
    ).catch(() => null);
    if (!subscriber) {
      throw new Error('BAD_AUTH: Subscriber creation failed');
    }

    socket.data = {
      widgetId: widget._id,
    };

    this.subscribers.set(socket.id, subscriber);
    subscriber.subscribe(widget.scopes);
    subscriber.start();
    subscriber.onEvent((topic, event) => {
      socket.emit('event:' + topic, event);
    });

    const sockets = this.sockets.get(widget._id.toString());
    if (!sockets) {
      this.sockets.set(integration.integrationId, new Set([socket]));
    } else {
      sockets.add(socket);
    }
  }

  public async disconnectSocket(socket: Socket) {
    const subscriber = this.subscribers.get(socket.id);
    if (!subscriber) {
      return;
    }

    subscriber.stop();
    this.subscribers.delete(socket.id);

    const { widgetId } = socket.data || {};
    if (widgetId) {
      const sockets = this.sockets.get(widgetId);
      if (sockets) {
        sockets.delete(socket);
      }
    }
  }

  public getSocketsByWidget(widgetId: string): Set<Socket> {
    return this.sockets.get(widgetId) || new Set();
  }
}
