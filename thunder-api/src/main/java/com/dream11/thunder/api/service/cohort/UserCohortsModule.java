package com.dream11.thunder.api.service.cohort;

import com.dream11.common.guice.VertxAbstractModule;
import com.dream11.thunder.api.service.UserCohortsClient;
import io.vertx.reactivex.core.Vertx;
import com.google.inject.Singleton;

public class UserCohortsModule extends VertxAbstractModule {

  public UserCohortsModule(Vertx vertx) {
    super(vertx);
  }

  @Override
  protected void bindConfiguration() {
    bind(UserCohortsClient.class).to(UserCohortsClientImpl.class).in(Singleton.class);
  }
}
