package com.staroverlay.eventsub.gateway;

import java.util.ArrayList;
import java.util.List;

import com.staroverlay.eventsub.entities.Widget;

import io.socket.socketio.server.SocketIoSocket;

public class SocketGateway {
  private SocketIoSocket socket;
  private String userId;
  private List<String> rooms;

  public SocketGateway(SocketIoSocket socket, Widget widget) {
    this.socket = socket;
    this.userId = widget.userId;
    this.rooms = new ArrayList<>();
  }

  public String getID() {
    return this.socket.getId();
  }

  public String getUserID() {
    return this.userId;
  }

  public List<String> getRooms() {
    return this.rooms;
  }

  public void joinRoom(String topic) {
    this.socket.joinRoom(topic);
  }
}
