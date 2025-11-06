package com.dream11.thunder.api.service.cache;

import com.dream11.thunder.api.config.AppConfig;
import com.dream11.thunder.api.config.CacheConfig;
import com.dream11.thunder.api.service.StaticDataCache;
import com.dream11.thunder.core.dao.BehaviourTagsRepository;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.util.MetricUtil;
import com.google.inject.Inject;
import com.timgroup.statsd.StatsDClient;
import io.reactivex.rxjava3.Completable;
import io.reactivex.rxjava3.Single;
import io.vertx.reactivex.core.Vertx;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class StaticDataCacheImpl implements StaticDataCache {

  private final StatsDClient d11DDClient;
  private final CTARepository ctaRepository;
  private final BehaviourTagsRepository behaviourTagsRepository;
  private MasterData masterDataCache;
  private final CacheConfig cacheConfig;
  private final AtomicBoolean isCacheInitiationProcessTriggered = new AtomicBoolean(false);
  private final CompletableFuture<Void> cacheInitiationProcess = new CompletableFuture<>();

  @Inject
  public StaticDataCacheImpl(
      StatsDClient d11DDClient,
      CTARepository ctaRepository,
      BehaviourTagsRepository behaviourTagsRepository,
      AppConfig config) {
    this.d11DDClient = d11DDClient;
    this.ctaRepository = ctaRepository;
    this.behaviourTagsRepository = behaviourTagsRepository;
    cacheConfig = config.getCache();
  }

  @Override
  public synchronized CompletableFuture<?> initiateCache() {
    if (isCacheInitiationProcessTriggered.get()) {
      log.info("cache initiation process already triggered...");
      return cacheInitiationProcess;
    }

    log.info("initiating cache...");
    populateCache()
        .subscribe(
            () -> {
              log.info("cache initiated...");
              scheduleCacheRefresh(cacheConfig.getRefresh().getInterval().getMs());
              cacheInitiationProcess.complete(null);
            },
            cacheInitiationProcess::completeExceptionally);

    isCacheInitiationProcessTriggered.set(true);
    return cacheInitiationProcess;
  }

  private Completable populateCache() {
    return Single.zip(
            ctaRepository.findAllWithStatusActive(),
            ctaRepository.findAllWithStatusPaused(),
            behaviourTagsRepository.findAll(),
            (activeCTAs, pausedCTAs, behaviourTags) -> {
              MasterData masterData = new MasterData();
              masterData.setActiveCTACache(activeCTAs);
              masterData.setPausedCTACache(pausedCTAs);
              masterData.setBehaviourTagCache(behaviourTags);
              return masterData;
            })
        .doOnSuccess(
            masterData -> {
              this.masterDataCache = masterData;
              log.info("cache updated...");
            })
        .doOnError(
            error -> {
              d11DDClient.incrementCounter(
                  MetricUtil.aspect("background_process_error", "process:cache_update"));
              log.error("error while updating cache", error);
            })
        .doOnSubscribe(disposable -> log.info("updating cache..."))
        .ignoreElement();
  }

  private void scheduleCacheRefresh(Long delay) {
    Vertx.currentContext().owner().setPeriodic(delay, timerId -> populateCache().subscribe());
  }

  @Override
  public Map<Long, CTA> findAllActiveCTA() {
    return masterDataCache.getActiveCTACache();
  }

  @Override
  public Map<Long, CTA> findAllPausedCTA() {
    return masterDataCache.getPausedCTACache();
  }

  @Override
  public Map<String, BehaviourTag> findAllBehaviourTags() {
    return masterDataCache.getBehaviourTagCache();
  }
}
