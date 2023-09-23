package com.staroverlay.eventsub;

import java.util.HashMap;
import java.util.Map;

import com.sammwy.milkshake.Milkshake;
import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.find.FindFilter;

import com.staroverlay.eventsub.entities.Integration;
import com.staroverlay.eventsub.entities.Widget;
import com.staroverlay.eventsub.gateway.SocketGateway;

import io.socket.socketio.server.SocketIoNamespace;

public abstract class AbstractManager {
  private String clientId;
  private String type;
  private SocketIoNamespace ns;

  private Map<String, AbstractClient> clients;

  public AbstractManager(String clientId, String type, SocketIoNamespace ns) {
    this.clientId = clientId;
    this.type = type;
    this.ns = ns;

    this.clients = new HashMap<>();
  }

  public abstract AbstractClient createClient(String clientId, String channelId, String accessToken);

  public void emit(String userId, Event event, Object eventData) {
    this.ns.broadcast(event.getID(userId), event.getID(), eventData);
  }

  public void addSocket(SocketGateway socket, Widget widget) throws Exception {
    AbstractClient client = this.clients.get(widget.userId);

    if (client == null) {
      Repository<Integration> integrations = Milkshake.getRepository(Integration.class);
      FindFilter filter = new FindFilter("userId", widget.userId).and().isEquals("type", this.type);
      Integration integration = integrations.findOne(filter);

      if (integration == null) {
        throw new Exception("Integration not found");
      }

      client = this.createClient(this.clientId, widget.userId, integration.accessToken);
      this.clients.put(widget.userId, client);
    }

    for (String scope : widget.scopes) {
      socket.joinRoom(client.subscribe(scope));
    }
  }

  public void removeSocket(SocketGateway socket) {
    AbstractClient client = this.clients.get(socket.getUserID());

    if (client == null) {
      return;
    }

    for (String room : socket.getRooms()) {
      client.unsubscribe(room);
    }

    if (!client.isActive()) {
      this.clients.remove(socket.getUserID());
    }
  }
}
