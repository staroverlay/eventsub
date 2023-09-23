package com.staroverlay.eventsub.entities;

import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;

public class Widget extends Entity {
  @Prop
  public String displayName;

  @Prop
  public String userId;

  @Prop
  public boolean enabled;

  @Prop
  public String token;

  @Prop
  public String templateId;

  @Prop
  public String templateRaw;

  @Prop
  public String settings;

  @Prop
  public String[] scopes;
}
