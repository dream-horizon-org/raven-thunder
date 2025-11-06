package com.dream11.thunder.api.config;

import com.dream11.thunder.core.config.Interval;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class UserCohortsConfig {

  private String host;
  private Integer port;
  private Interval timeout;
}
