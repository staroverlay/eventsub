package com.staroverlay.eventsub.entities;

import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;

public class Integration extends Entity {
  @Prop
  public String accessToken;

  @Prop
  public String refreshToken;

  @Prop
  public String ownerId;

  @Prop
  public String avatar;

  @Prop
  public String integrationId;

  @Prop
  public String username;

  @Prop
  public String type;

  @Prop
  public long expires;
}
