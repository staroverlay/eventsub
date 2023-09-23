package com.staroverlay.eventsub;

import io.socket.engineio.server.EngineIoServer;
import io.socket.socketio.server.SocketIoServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ServerWrapper {
  private final Server server;

  private final EngineIoServer engineIO;
  private final SocketIoServer socketIO;

  public ServerWrapper(String ip, int port, String[] allowedCorsOrigins) {
    this.server = new Server(new InetSocketAddress(ip, port));
    this.engineIO = new EngineIoServer();
    this.socketIO = new SocketIoServer(engineIO);

    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
    System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

    ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    servletContextHandler.setContextPath("/");

    final JettyWebSocketServlet servlet = new JettyWebSocketServlet() {
      private static final long serialVersionUID = 4525525859144703715L;

      @Override
      protected void configure(JettyWebSocketServletFactory jettyWebSocketServletFactory) {
        jettyWebSocketServletFactory.addMapping(
            "/",
            (request, response) -> new JettyEngineIoWebSocketHandler(engineIO));
      }

      @Override
      public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (request instanceof HttpServletRequest) {
          final String upgradeHeader = ((HttpServletRequest) request).getHeader("upgrade");
          if (upgradeHeader != null) {
            super.service(request, response);
          } else {
            engineIO.handleRequest((HttpServletRequest) request, (HttpServletResponse) response);
          }
        } else {
          super.service(request, response);
        }
      }
    };

    final ServletHolder servletHolder = new ServletHolder(servlet);
    servletHolder.setAsyncSupported(false);
    servletContextHandler.addServlet(servletHolder, "/socket.io/*");
    JettyWebSocketServletContainerInitializer.configure(servletContextHandler, null);

    HandlerList handlerList = new HandlerList();
    handlerList.setHandlers(new Handler[] { servletContextHandler });
    this.server.setHandler(handlerList);
  }

  public void startServer() throws Exception {
    server.start();
  }

  public void stopServer() throws Exception {
    server.stop();
  }

  public SocketIoServer getSocketIoServer() {
    return socketIO;
  }
}
