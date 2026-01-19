package com.raven.thunder.api.service;

import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.TestCTA;
import java.util.List;
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

  /** Returns a map of test CTAs keyed by tenantId:userId. */
  Map<String, List<TestCTA>> fetchUserTestCtaMap();
}
