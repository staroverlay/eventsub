package com.staroverlay.eventsub.twitch;

import java.util.HashMap;
import java.util.Map;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.pubsub.PubSubSubscription;
import com.github.twitch4j.pubsub.enums.PubSubType;
import com.staroverlay.eventsub.AbstractClient;

public class TwitchPubSubClient extends AbstractClient {
  public static final TwitchClient CLIENT = TwitchClientBuilder.builder().withEnablePubSub(true).build();

  private OAuth2Credential credentials;
  private String channelId = null;
  private Map<String, PubSubSubscription> subscriptions;

  public TwitchPubSubClient(String clientId, String channelId, String accessToken) {
    this.credentials = new OAuth2Credential(clientId, accessToken);
    this.channelId = channelId;
    this.subscriptions = new HashMap<>();
  }

  @Override
  public String getID() {
    return this.channelId;
  }

  @Override
  public boolean isActive() {
    return this.subscriptions.size() > 0;
  }

  @Override
  public boolean isSubscribed(String id) {
    return this.subscriptions.containsKey(id);
  }

  @Override
  public void onSubscribe(String topic) {
    PubSubSubscription sub = CLIENT.getPubSub().listenOnTopic(
        PubSubType.LISTEN,
        this.credentials,
        topic);
    this.subscriptions.put(topic, sub);
  }

  @Override
  public void onUnsubscribe(String id) {
    PubSubSubscription sub = this.subscriptions.get(id);
    CLIENT.getPubSub().unsubscribeFromTopic(sub);
    this.subscriptions.remove(id);
  }
}
