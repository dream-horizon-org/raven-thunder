package com.dream11.thunder.api;

import com.dream11.aerospike.reactivex.client.AerospikeClient;
import com.dream11.common.guice.VertxAbstractModule;
import com.dream11.common.util.ContextUtils;
import com.dream11.thunder.api.config.AppConfig;
import com.dream11.thunder.api.config.AppConfigProvider;
import com.dream11.thunder.api.config.ResilienceConfig;
import com.dream11.thunder.api.util.SDUICircuitBreaker;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.webclient.reactivex.client.WebClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.vertx.reactivex.core.Vertx;

public class MainModule extends VertxAbstractModule {

  private final Vertx vertx;

  public MainModule(Vertx vertx) {
    super(vertx);
    this.vertx = vertx;
  }

  @Override
  protected void bindConfiguration() {
    bind(AppConfig.class).toProvider(AppConfigProvider.class);
    bind(ResilienceConfig.class).toProvider(() -> ContextUtils.getInstance(ResilienceConfig.class));
    bind(AerospikeConfig.class).toProvider(() -> AppConfigProvider.getAppConfig().getAerospike());
    bind(AerospikeClient.class).toProvider(() -> ContextUtils.getInstance(AerospikeClient.class));
    bind(WebClient.class).toProvider(() -> ContextUtils.getInstance(WebClient.class));
    bind(StatsDClient.class)
        .toProvider(() -> ContextUtils.getInstance(NonBlockingStatsDClient.class, "d11DDClient"));
    bind(SDUICircuitBreaker.class)
        .toProvider(() -> ContextUtils.getInstance(SDUICircuitBreaker.class));
  }
}
