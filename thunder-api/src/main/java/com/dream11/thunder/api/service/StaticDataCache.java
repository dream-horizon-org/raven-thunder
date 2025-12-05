package com.dream11.thunder.api.service;

import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Simple in-memory cache for active/paused CTAs and behaviour tags used by SDK flows. */
public interface StaticDataCache {

  /**
   * Initializes cache by loading data from repositories.
   *
   * @return future completing when all datasets are loaded
   */
  CompletableFuture<?> initiateCache();

  /** Returns a snapshot of active CTAs keyed by CTA id. */
  Map<Long, CTA> findAllActiveCTA();

  /** Returns a snapshot of paused CTAs keyed by CTA id. */
  Map<Long, CTA> findAllPausedCTA();

  /** Returns a snapshot of behaviour tags keyed by tag name. */
  Map<String, BehaviourTag> findAllBehaviourTags();
}
