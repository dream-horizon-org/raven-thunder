package com.raven.thunder.api.service.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.raven.thunder.api.service.StaticDataCache;
import com.raven.thunder.core.config.Config;
import com.raven.thunder.core.dao.BehaviourTagsRepository;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.dao.TestCTARepository;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.TestCTA;
import com.raven.thunder.core.util.SharedDataUtils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.Vertx;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

/**
 * Cache implementation for static data (CTAs and BehaviourTags). Loads data from repositories on
 * initialization and refreshes periodically based on configuration.
 */
@Slf4j
@Singleton
public class StaticDataCacheImpl implements StaticDataCache {

  private final CTARepository ctaRepository;
  private final BehaviourTagsRepository behaviourTagsRepository;
  private final TestCTARepository testCTARepository;
  private final Vertx vertx;
  private static final long DEFAULT_CACHE_REFRESH_INTERVAL_MS = 30000L; // 30 seconds default
  private MasterData masterDataCache;
  private final AtomicBoolean isCacheInitiationProcessTriggered = new AtomicBoolean(false);
  private final CompletableFuture<Void> cacheInitiationProcess = new CompletableFuture<>();

  @Inject
  public StaticDataCacheImpl(
      CTARepository ctaRepository,
      BehaviourTagsRepository behaviourTagsRepository,
      TestCTARepository testCTARepository,
      Vertx vertx) {
    this.ctaRepository = ctaRepository;
    this.behaviourTagsRepository = behaviourTagsRepository;
    this.testCTARepository = testCTARepository;
    this.vertx = vertx;
    // Initialize with empty data to avoid NPE
    this.masterDataCache = new MasterData();
    this.masterDataCache.setActiveCTACache(Collections.emptyMap());
    this.masterDataCache.setPausedCTACache(Collections.emptyMap());
    this.masterDataCache.setBehaviourTagCache(Collections.emptyMap());
    this.masterDataCache.setTestCTACache(Collections.emptyMap());
  }

  @Override
  public synchronized CompletableFuture<?> initiateCache() {
    if (isCacheInitiationProcessTriggered.get()) {
      log.info("Cache initiation process already triggered...");
      return cacheInitiationProcess;
    }

    log.info("Initiating cache...");
    populateCache()
        .subscribe(
            () -> {
              log.info("Cache initiated...");
              Long refreshInterval = getCacheRefreshInterval();
              if (refreshInterval != null && refreshInterval > 0) {
                scheduleCacheRefresh(refreshInterval);
              } else {
                log.warn(
                    "Cache refresh interval not configured or invalid, cache will not refresh periodically");
              }
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
            testCTARepository.fetchAllTestCTAs(),
            (activeCTAs, pausedCTAs, behaviourTags, testCTAs) -> {
              MasterData masterData = new MasterData();
              masterData.setActiveCTACache(activeCTAs);
              masterData.setPausedCTACache(pausedCTAs);
              masterData.setBehaviourTagCache(behaviourTags);
              masterData.setTestCTACache(testCTAs);
              return masterData;
            })
        .doOnSuccess(
            masterData -> {
              this.masterDataCache = masterData;
              log.info("Cache updated...");
            })
        .doOnError(
            error -> {
              log.error("Error while updating cache", error);
            })
        .doOnSubscribe(disposable -> log.info("Updating cache..."))
        .ignoreElement();
  }

  private void scheduleCacheRefresh(Long delay) {
    vertx.setPeriodic(
        delay,
        timerId -> {
          log.debug("Refreshing cache...");
          populateCache().subscribe();
        });
    log.info("Cache refresh scheduled with interval: {} ms", delay);
  }

  private Long getCacheRefreshInterval() {
    // Try to get from Config if available, otherwise use default
    try {
      Config config = SharedDataUtils.get(vertx.getDelegate(), Config.class);
      if (config != null && config.getCacheRefresh() != null) {
        Long ms = config.getCacheRefresh().getMs();
        if (ms != null) {
          return ms;
        }
      }
    } catch (Exception e) {
      log.debug(
          "Could not retrieve Config from SharedData, using default cache refresh interval", e);
    }
    log.info("Using default cache refresh interval: {} ms", DEFAULT_CACHE_REFRESH_INTERVAL_MS);
    return DEFAULT_CACHE_REFRESH_INTERVAL_MS;
  }

  @Override
  public Map<Long, CTA> findAllActiveCTA() {
    if (masterDataCache == null || masterDataCache.getActiveCTACache() == null) {
      return Collections.emptyMap();
    }
    return masterDataCache.getActiveCTACache();
  }

  @Override
  public Map<Long, CTA> findAllPausedCTA() {
    if (masterDataCache == null || masterDataCache.getPausedCTACache() == null) {
      return Collections.emptyMap();
    }
    return masterDataCache.getPausedCTACache();
  }

  @Override
  public Map<String, BehaviourTag> findAllBehaviourTags() {
    if (masterDataCache == null || masterDataCache.getBehaviourTagCache() == null) {
      return Collections.emptyMap();
    }
    return masterDataCache.getBehaviourTagCache();
  }

  @Override
  public Map<String, List<TestCTA>> fetchUserTestCtaMap() {
    if (masterDataCache == null || masterDataCache.getTestCTACache() == null) {
      return Collections.emptyMap();
    }
    return masterDataCache.getTestCTACache();
  }
}
