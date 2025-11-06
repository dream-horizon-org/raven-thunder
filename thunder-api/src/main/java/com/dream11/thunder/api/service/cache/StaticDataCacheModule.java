package com.dream11.thunder.api.service.cache;

import com.dream11.common.guice.VertxAbstractModule;
import com.dream11.thunder.api.service.StaticDataCache;
import io.vertx.reactivex.core.Vertx;
import com.google.inject.Singleton;

public class StaticDataCacheModule extends VertxAbstractModule {

  public StaticDataCacheModule(Vertx vertx) {
    super(vertx);
  }

  @Override
  protected void bindConfiguration() {
    bind(StaticDataCache.class).to(StaticDataCacheImpl.class).in(Singleton.class);
  }
}
