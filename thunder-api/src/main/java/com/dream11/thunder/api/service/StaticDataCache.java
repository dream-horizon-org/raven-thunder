package com.dream11.thunder.api.service;

import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface StaticDataCache {

  CompletableFuture<?> initiateCache();

  Map<Long, CTA> findAllActiveCTA();

  Map<Long, CTA> findAllPausedCTA();

  Map<String, BehaviourTag> findAllBehaviourTags();
}
