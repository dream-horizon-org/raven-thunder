package com.dream11.thunder.api.service.debug;

import com.dream11.common.guice.VertxAbstractModule;
import com.dream11.thunder.api.service.AppDebugService;
import io.vertx.reactivex.core.Vertx;
import com.google.inject.Singleton;

public class AppDebugModule extends VertxAbstractModule {

  public AppDebugModule(Vertx vertx) {
    super(vertx);
  }

  @Override
  protected void bindConfiguration() {
    bind(AppDebugService.class).to(AppDebugServiceImpl.class).in(Singleton.class);
  }
}
