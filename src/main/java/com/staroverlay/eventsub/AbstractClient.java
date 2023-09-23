package com.staroverlay.eventsub;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractClient {
  private Map<String, Integer> refCount = new HashMap<>();

  public abstract String getID();

  public abstract boolean isActive();

  public abstract boolean isSubscribed(String id);

  public abstract void onSubscribe(String topic);

  public String subscribe(String topic) {
    String id = topic + "." + this.getID();

    if (this.isSubscribed(id)) {
      int newRefs = this.refCount.get(topic) + 1;
      this.refCount.put(topic, newRefs);
      Logger.debug("Added subscription ref to " + id + "(" + newRefs + ")");
      return id;
    }

    Logger.debug("New subscriptions to " + id);
    this.refCount.put(id, 1);
    this.onSubscribe(topic);
    return id;
  }

  public abstract void onUnsubscribe(String id);

  public void unsubscribe(String id) {
    if (this.refCount.containsKey(id)) {
      int count = this.refCount.get(id);
      if (count > 1) {
        int newRefs = count - 1;
        this.refCount.put(id, newRefs);
        Logger.debug("Removed subscription ref to " + id + "(" + (newRefs) + ")");
        return;
      }
      this.refCount.remove(id);
      Logger.debug("Removed subscription to " + id);
    }

    if (this.isSubscribed(id)) {
      this.onUnsubscribe(id);
    }
  }
}
