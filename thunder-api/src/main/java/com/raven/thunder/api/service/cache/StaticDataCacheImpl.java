package com.raven.thunder.api.service.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.raven.thunder.api.service.StaticDataCache;
import com.raven.thunder.core.dao.BehaviourTagsRepository;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple cache implementation for static data (CTAs and BehaviourTags). Loads data from
 * repositories on initialization.
 */
@Slf4j
@Singleton
public class StaticDataCacheImpl implements StaticDataCache {

  private final CTARepository ctaRepository;
  private final BehaviourTagsRepository behaviourTagsRepository;

  private final Map<Long, CTA> activeCTACache = new ConcurrentHashMap<>();
  private final Map<Long, CTA> pausedCTACache = new ConcurrentHashMap<>();
  private final Map<String, BehaviourTag> behaviourTagCache = new ConcurrentHashMap<>();

  @Inject
  public StaticDataCacheImpl(
      CTARepository ctaRepository, BehaviourTagsRepository behaviourTagsRepository) {
    this.ctaRepository = ctaRepository;
    this.behaviourTagsRepository = behaviourTagsRepository;
  }

  @Override
  public CompletableFuture<?> initiateCache() {
    log.info("Initializing static data cache...");

    CompletableFuture<Void> activeCTAFuture = loadActiveCTAs();
    CompletableFuture<Void> pausedCTAFuture = loadPausedCTAs();
    CompletableFuture<Void> behaviourTagsFuture = loadBehaviourTags();

    return CompletableFuture.allOf(activeCTAFuture, pausedCTAFuture, behaviourTagsFuture)
        .thenRun(() -> log.info("Static data cache initialized successfully"));
  }

  private CompletableFuture<Void> loadActiveCTAs() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    ctaRepository
        .findAllWithStatusActive()
        .doOnSuccess(
            ctas -> {
              activeCTACache.clear();
              activeCTACache.putAll(ctas);
              log.info("Loaded {} active CTAs into cache", ctas.size());
            })
        .ignoreElement()
        .subscribe(() -> future.complete(null), future::completeExceptionally);
    return future;
  }

  private CompletableFuture<Void> loadPausedCTAs() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    ctaRepository
        .findAllWithStatusPaused()
        .doOnSuccess(
            ctas -> {
              pausedCTACache.clear();
              pausedCTACache.putAll(ctas);
              log.info("Loaded {} paused CTAs into cache", ctas.size());
            })
        .ignoreElement()
        .subscribe(() -> future.complete(null), future::completeExceptionally);
    return future;
  }

  private CompletableFuture<Void> loadBehaviourTags() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    behaviourTagsRepository
        .findAll()
        .doOnSuccess(
            tags -> {
              behaviourTagCache.clear();
              behaviourTagCache.putAll(tags);
              log.info("Loaded {} behaviour tags into cache", tags.size());
            })
        .ignoreElement()
        .subscribe(() -> future.complete(null), future::completeExceptionally);
    return future;
  }

  @Override
  public Map<Long, CTA> findAllActiveCTA() {
    return new ConcurrentHashMap<>(activeCTACache);
  }

  @Override
  public Map<Long, CTA> findAllPausedCTA() {
    return new ConcurrentHashMap<>(pausedCTACache);
  }

  @Override
  public Map<String, BehaviourTag> findAllBehaviourTags() {
    return new ConcurrentHashMap<>(behaviourTagCache);
  }
}
