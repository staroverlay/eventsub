package com.staroverlay.eventsub;

public enum Event {
  TWITCH_BITS_BADGE_UNLOCK("twitch:cheer", "channel-bits-badge-unlocks"),
  TWITCH_BITS("twitch:cheer", "channel-bits-events-v2"),
  TWITCH_FOLLOW("twitch:follow", "following"),
  TWITCH_GOAL("twitch:goal", "creator-goals-events-v1"),
  TWITCH_HYPE_TRAIN("twitch:hype_train", "hype-train-events-v1"),
  TWITCH_HYPE_TRAIN_REWARD("twitch:hype_train", "hype-train-events-v1.rewards"),
  TWITCH_POLL("twitch:poll", "polls"),
  TWITCH_RAID_TO("twitch:raid", "raid"),
  TWITCH_RAID_FROM("twitch:raid", "raid"),
  TWITCH_REDEMPTION("twitch:redemption", "community-points-channel-v1"),
  TWITCH_SHOUTOUT("twitch:shoutout", "shoutout"),
  TWITCH_SUBSCRIPTION("twitch:subscription", "channel-subscribe-events-v1"),
  TWITCH_SUBSCRIPTION_GIFT("twitch:subscription", "channel-sub-gifts-v1"),
  TWITCH_VIDEO_PLAYBACK("twitch:stream", "video-playback-by-id");

  private final String scope;
  private final String topic;

  Event(String scope, String topic) {
    this.scope = scope;
    this.topic = topic;
  }

  public String getID() {
    return this.name().toLowerCase();
  }

  public String getID(String channelID) {
    return this.getID() + "." + channelID;
  }

  public String getPlatform() {
    return this.name().split("_")[0].toLowerCase();
  }

  public String getScope() {
    return scope;
  }

  public String getTopic() {
    return topic;
  }
}
