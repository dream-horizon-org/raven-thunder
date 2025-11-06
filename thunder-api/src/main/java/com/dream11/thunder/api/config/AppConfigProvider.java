package com.dream11.thunder.api.config;

import com.dream11.common.app.AppContext;
import com.dream11.common.util.ConfigProvider;
import com.google.inject.Provider;
import lombok.SneakyThrows;

public class AppConfigProvider implements Provider<AppConfig> {

  private static AppConfig appConfig;

  @Override
  public AppConfig get() {
    if (appConfig == null) {
      setAppConfig();
    }

    return appConfig;
  }

  @SneakyThrows
  public static AppConfig getAppConfig() {
    if (appConfig == null) {
      setAppConfig();
    }

    return appConfig;
  }

  @SneakyThrows
  public static synchronized void setAppConfig() {
    if (appConfig == null) {
      appConfig =
          AppContext.getInstance(ConfigProvider.class)
              .getConfig("config/application", "application", AppConfig.class);
    }
  }
}
