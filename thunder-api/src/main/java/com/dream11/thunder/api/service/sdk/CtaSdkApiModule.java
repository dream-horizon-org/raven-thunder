package com.dream11.thunder.api.service.sdk;

import com.dream11.common.guice.VertxAbstractModule;
import com.dream11.thunder.api.service.SdkService;
import io.vertx.reactivex.core.Vertx;
import com.google.inject.Singleton;

public class CtaSdkApiModule extends VertxAbstractModule {

  public CtaSdkApiModule(Vertx vertx) {
    super(vertx);
  }

  @Override
  protected void bindConfiguration() {
    bind(SdkService.class).to(SdkServiceImpl.class).in(Singleton.class);
  }
}
