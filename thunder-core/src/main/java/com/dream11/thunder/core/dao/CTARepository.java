package com.dream11.thunder.core.dao;

import com.dream11.thunder.core.dao.cta.ActiveCTA;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.dao.cta.ScheduledCTA;
import com.dream11.thunder.core.io.response.FilterResponse;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;

public interface CTARepository {

  Completable create(String tenantId, CTA cta);

  Single<Long> generatedIncrementId(String tenantId);

  Maybe<CTA> find(String tenantId, Long id);

  Maybe<CTADetails> findWithGeneration(String tenantId, Long id);

  Single<Map<Long, CTA>> findAll(String tenantId);

  Single<Map<Long, CTA>> findAllWithStatusActive();

  Single<Map<Long, CTA>> findAllWithStatusPaused();

  Single<Map<Long, ActiveCTA>> findAllIdsWithStatusLive();

  Single<Map<Long, ActiveCTA>> findAllIdsWithStatusPaused();

  Maybe<FilterResponse> findFilters(String tenantId);

  Single<Map<Long, ScheduledCTA>> findAllWithStatusScheduled();

  Completable update(Long id, CTAStatus status, Long startTime, Long endTime);

  Completable update(Long id, CTAStatus status);

  Completable update(Long id, int generation, CTAStatus status);

  Completable update(Long id, List<String> behaviourTag);

  Completable update(CTA cta, int generation);

  Completable terminateOrConclude(Long id, CTAStatus status, Long startTime, Long endTime);

  Completable terminateOrConclude(Long id, CTAStatus status, Long endTime);

  Completable updateFilters(
      String tenantId, List<String> tags, String team, String name, String createdBy);
}

