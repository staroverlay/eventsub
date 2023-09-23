package com.staroverlay.eventsub;

import io.github.cdimascio.dotenv.Dotenv;

public class Environment {
  public static String ENV;
  public static String HOST;
  public static int PORT;
  public static String MONGODB_URI;
  public static String RENDERER_SERVER;
  public static String TWITCH_CLIENT_ID;

  public static boolean isDevelopment() {
    return ENV.equals("development");
  }

  public static void load() {
    Dotenv dotenv = Dotenv.load();
    dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

    Environment.HOST = System.getProperty("ENV", "production");
    Environment.HOST = System.getProperty("HOST", "127.0.0.1");
    Environment.PORT = Integer.parseInt(System.getProperty("PORT", "3000"));
    Environment.MONGODB_URI = System.getProperty("MONGODB_URI");
    Environment.RENDERER_SERVER = System.getProperty("RENDERER_SERVER");
    Environment.TWITCH_CLIENT_ID = System.getProperty("TWITCH_CLIENT_ID");
  }
}
