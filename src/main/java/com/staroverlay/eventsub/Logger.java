package com.staroverlay.eventsub;

public class Logger {
  public static void print(String prefix, String message) {
    String time = java.time.LocalTime.now().toString();
    System.out.println(prefix + " " + time + " " + message);
  }

  public static void info(String message) {
    print("INFO", message);
  }

  public static void crit(String message) {
    print("CRIT", message);
  }

  public static void crit(Exception e) {
    crit(e.getMessage());
  }

  public static void warn(String message) {
    print("WARN", message);
  }

  public static void debug(String message) {
    if (Environment.isDevelopment()) {
      print("DBUG", message);
    }
  }
}
