package com.staroverlay.eventsub.twitch;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.pubsub.events.ChannelBitsBadgeUnlockEvent;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.CreatorGoalEvent;
import com.github.twitch4j.pubsub.events.FollowingEvent;
import com.github.twitch4j.pubsub.events.HypeTrainEndEvent;
import com.github.twitch4j.pubsub.events.HypeTrainLevelUpEvent;
import com.github.twitch4j.pubsub.events.HypeTrainProgressionEvent;
import com.github.twitch4j.pubsub.events.HypeTrainStartEvent;
import com.github.twitch4j.pubsub.events.PollsEvent;
import com.github.twitch4j.pubsub.events.RaidGoEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import com.staroverlay.eventsub.AbstractClient;
import com.staroverlay.eventsub.AbstractManager;
import com.staroverlay.eventsub.Event;

import io.socket.socketio.server.SocketIoNamespace;

public class TwitchManager extends AbstractManager {
  public TwitchManager(String clientId, SocketIoNamespace ns) {
    super(clientId, "twitch", ns);

    EventManager ev = TwitchPubSubClient.CLIENT.getEventManager();
    ev.onEvent(ChannelBitsBadgeUnlockEvent.class, this::onBitsBadgeUnlock);
    ev.onEvent(ChannelBitsEvent.class, this::onBits);
    ev.onEvent(FollowingEvent.class, this::onFollow);
    ev.onEvent(CreatorGoalEvent.class, this::onCreatorGoal);
    ev.onEvent(HypeTrainStartEvent.class, this::onHypeTrainStart);
    ev.onEvent(HypeTrainProgressionEvent.class, this::onHypeTrainProgression);
    ev.onEvent(HypeTrainLevelUpEvent.class, this::onHypeTrainLevelUp);
    ev.onEvent(HypeTrainEndEvent.class, this::onHypeTrainEnd);
    ev.onEvent(PollsEvent.class, this::onPoll);
    ev.onEvent(RaidGoEvent.class, this::onRaid);
    ev.onEvent(RewardRedeemedEvent.class, this::onRewardRedeemed);

    TwitchPubSubClient.CLIENT.getPubSub().listenForWhisperEvents(null, clientId);
  }

  private void onBitsBadgeUnlock(ChannelBitsBadgeUnlockEvent event) {
    this.emit(event.getData().getChannelId(), Event.TWITCH_BITS_BADGE_UNLOCK, event.getData());
  }

  private void onBits(ChannelBitsEvent event) {
    this.emit(event.getData().getChannelId(), Event.TWITCH_BITS, event.getData());
  }

  private void onFollow(FollowingEvent event) {
    this.emit(event.getChannelId(), Event.TWITCH_FOLLOW, event.getData());
  }

  private void onCreatorGoal(CreatorGoalEvent event) {
    this.emit(event.getChannelId(), Event.TWITCH_GOAL, event.getGoal());
  }

  private void onHypeTrainStart(HypeTrainStartEvent event) {
    this.emit(event.getData().getChannelId(), Event.TWITCH_HYPE_TRAIN, event.getData());
  }

  private void onHypeTrainProgression(HypeTrainProgressionEvent event) {
    this.emit(event.getChannelId(), Event.TWITCH_HYPE_TRAIN, event.getData());
  }

  private void onHypeTrainLevelUp(HypeTrainLevelUpEvent event) {
    this.emit(event.getChannelId(), Event.TWITCH_HYPE_TRAIN, event.getData());
  }

  private void onHypeTrainEnd(HypeTrainEndEvent event) {
    this.emit(event.getChannelId(), Event.TWITCH_HYPE_TRAIN, event.getData());
  }

  private void onPoll(PollsEvent event) {
    this.emit(event.getData().getOwnedBy(), Event.TWITCH_POLL, event.getData());
  }

  private void onRaid(RaidGoEvent event) {
    this.emit(event.getRaid().getSourceId(), Event.TWITCH_RAID_TO, event.getRaid());
    this.emit(event.getRaid().getTargetId(), Event.TWITCH_RAID_FROM, event.getRaid());
  }

  private void onRewardRedeemed(RewardRedeemedEvent event) {
    this.emit(event.getRedemption().getChannelId(), Event.TWITCH_REDEMPTION, event.getRedemption());
  }

  @Override
  public AbstractClient createClient(String clientId, String channelId, String accessToken) {
    return new TwitchPubSubClient(clientId, channelId, accessToken);
  }
}
