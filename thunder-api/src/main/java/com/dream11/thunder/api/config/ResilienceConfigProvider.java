package com.dream11.thunder.api.config;

import com.dream11.common.app.AppContext;
import com.dream11.common.util.ConfigProvider;
import com.google.inject.Provider;
import lombok.SneakyThrows;

public class ResilienceConfigProvider implements Provider<ResilienceConfig> {

  private static ResilienceConfig resilienceConfig;

  @Override
  public ResilienceConfig get() {
    if (resilienceConfig == null) {
      setResilienceConfig();
    }

    return resilienceConfig;
  }

  @SneakyThrows
  public static synchronized void setResilienceConfig() {
    if (resilienceConfig == null) {
      resilienceConfig =
          AppContext.getInstance(ConfigProvider.class)
              .getConfig("config/resilience", "resilience", ResilienceConfig.class);
    }
  }
}
