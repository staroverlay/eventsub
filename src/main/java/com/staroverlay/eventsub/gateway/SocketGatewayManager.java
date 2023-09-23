package com.staroverlay.eventsub.gateway;

import java.util.HashMap;
import java.util.Map;

import com.sammwy.milkshake.Milkshake;
import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.find.FindFilter;
import com.staroverlay.eventsub.Environment;
import com.staroverlay.eventsub.Logger;
import com.staroverlay.eventsub.entities.Widget;
import com.staroverlay.eventsub.twitch.TwitchManager;

import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoSocket;

public class SocketGatewayManager {
  private Map<String, SocketGateway> sockets;

  private TwitchManager twitch;

  public SocketGatewayManager(SocketIoNamespace ns) {
    this.sockets = new HashMap<>();
    this.twitch = new TwitchManager(System.getProperty("TWITCH_CLIENT_ID"), ns);
  }

  public void handleOnDisconnect(SocketIoSocket socket) {
    SocketGateway gateway = this.sockets.get(socket.getId());
    if (gateway == null) {
      return;
    }

    this.twitch.removeSocket(gateway);
    this.sockets.remove(socket.getId());
  }

  public void handleOnConnect(SocketIoSocket socket) {
    Repository<Widget> widgets = Milkshake.getRepository(Widget.class);

    socket.on("disconnect", (reason) -> {
      Logger.debug("Socket " + socket.getId() + " disconnected: " + reason);
      this.handleOnDisconnect(socket);
    });

    socket.on("auth", (token) -> {
      Widget widget = widgets.findOne(new FindFilter("token", token));
      if (widget == null) {
        Logger.debug("Socket " + socket.getId() + " BAD_AUTH: Invalid token");
        socket.emit("error", "BAD_AUTH: Invalid token");
        socket.disconnect(false);
        return;
      }

      SocketGateway gateway = new SocketGateway(socket, widget);
      String name = widget.displayName + " (" + widget.getID() + ")";
      Logger.debug("Socket " + socket.getId() + " authenticated as " + name);

      try {
        this.twitch.addSocket(gateway, widget);
        this.sockets.put(socket.getId(), gateway);
      } catch (Exception e) {
        socket.emit("error", "BAD_AUTH: " + e.getMessage());
        Logger.debug("Socket " + socket.getId() + " BAD_AUTH:  " + e.getMessage());
        if (Environment.isDevelopment()) {
          e.printStackTrace();
        }
      }
    });
  }
}
