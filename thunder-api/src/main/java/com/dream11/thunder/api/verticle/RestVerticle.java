package com.dream11.thunder.api.verticle;

import com.dream11.aerospike.reactivex.client.AerospikeClient;
import com.dream11.common.app.AppContext;
import com.dream11.common.util.ContextUtils;
import com.dream11.rest.AbstractRestVerticle;
import com.dream11.thunder.api.config.ResilienceConfig;
import com.dream11.thunder.api.config.ResilienceConfigProvider;
import com.dream11.thunder.api.service.StaticDataCache;
import com.dream11.thunder.api.util.SDUICircuitBreaker;
import com.dream11.webclient.reactivex.client.WebClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import io.reactivex.rxjava3.Completable;
import io.reactivex.rxjava3.Maybe;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestVerticle extends AbstractRestVerticle {

  private AerospikeClient d11AerospikeClient;
  private WebClient d11WebClient;

  public RestVerticle() {
    super("com.dream11.thunder.api");
  }

  @Override
  public Completable rxStart() {
    initiateDDClient();

    this.d11AerospikeClient = AerospikeClient.create(vertx);
    ContextUtils.setInstance(d11AerospikeClient);

    this.d11WebClient = WebClient.create(vertx);
    ContextUtils.setInstance(d11WebClient);

    StaticDataCache cache = AppContext.getInstance(StaticDataCache.class);
    CompletableFuture<?> cacheInitiationProcess = cache.initiateCache();

    ResilienceConfig resilienceConfig = new ResilienceConfigProvider().get();
    ContextUtils.setInstance(resilienceConfig);

    SDUICircuitBreaker sduiCircuitBreaker = new SDUICircuitBreaker(resilienceConfig);
    ContextUtils.setInstance(sduiCircuitBreaker);

    Maybe<Void> cacheInitiationProcessResult =
        vertx.rxExecuteBlocking(
            (Promise<Void> promise) -> {
              try {
                cacheInitiationProcess.get();
                promise.complete();
              } catch (Exception e) {
                promise.fail(e);
              }
            });

    return cacheInitiationProcessResult.ignoreElement().andThen(Completable.defer(super::rxStart));
  }

  @Override
  public Completable rxStop() {
    d11AerospikeClient.close();
    d11WebClient.close();

    StatsDClient d11DDClient =
        ContextUtils.getInstance(NonBlockingStatsDClient.class, "d11DDClient");
    d11DDClient.close();
    return super.rxStop();
  }

  private void initiateDDClient() {
    StatsDClient d11DDClient =
        new NonBlockingStatsDClientBuilder().hostname("localhost").port(8125).build();
    ContextUtils.setInstance(Vertx.currentContext(), d11DDClient, "d11DDClient");
  }

  @Override
  protected void initDDClient() {}
}
