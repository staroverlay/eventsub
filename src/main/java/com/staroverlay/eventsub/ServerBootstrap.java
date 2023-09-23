package com.staroverlay.eventsub;

import com.sammwy.milkshake.Milkshake;
import com.staroverlay.eventsub.gateway.SocketGatewayManager;

import io.socket.engineio.server.Emitter;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoSocket;

public class ServerBootstrap {
  private static String[] getCors() {
    return new String[] { Environment.RENDERER_SERVER };
  }

  public static void main(String[] args) throws Exception {
    // Load .env file.
    Environment.load();

    // Connect to Database.
    Milkshake.connect(Environment.MONGODB_URI);

    // Create http server.
    ServerWrapper serverWrapper = new ServerWrapper(Environment.HOST, Environment.PORT, getCors());
    serverWrapper.startServer();

    SocketIoServer server = serverWrapper.getSocketIoServer();
    SocketIoNamespace ns = server.namespace("/");

    // Initialize manager.
    SocketGatewayManager gatewayManager = new SocketGatewayManager(ns);

    // Listen for connections.
    ns.on("connection", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        SocketIoSocket socket = (SocketIoSocket) args[0];
        Logger.debug("New socket connected: " + socket.getId());
        gatewayManager.handleOnConnect(socket);
      }
    });
    Logger.info("Server started");
  }
}
