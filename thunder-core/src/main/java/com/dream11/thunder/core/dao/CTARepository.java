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

/** Repository for CTA CRUD, status transitions and metadata. */
public interface CTARepository {

  /** Creates a CTA for the tenant. */
  Completable create(String tenantId, CTA cta);

  /** Returns next auto-increment id for CTA in the tenant scope. */
  Single<Long> generatedIncrementId(String tenantId);

  /** Finds CTA by id. */
  Maybe<CTA> find(String tenantId, Long id);

  /** Finds CTA with generation id for optimistic concurrency control. */
  Maybe<CTADetails> findWithGeneration(String tenantId, Long id);

  /** Lists all CTAs for a tenant. */
  Single<Map<Long, CTA>> findAll(String tenantId);

  /** Lists all CTAs with status LIVE across tenants (for caching). */
  Single<Map<Long, CTA>> findAllWithStatusActive();

  /** Lists all CTAs with status PAUSED across tenants (for caching). */
  Single<Map<Long, CTA>> findAllWithStatusPaused();

  /** Lists ids and generations for LIVE CTAs (activation/expiry jobs). */
  Single<Map<Long, ActiveCTA>> findAllIdsWithStatusLive();

  /** Lists ids and generations for PAUSED CTAs (expiry jobs). */
  Single<Map<Long, ActiveCTA>> findAllIdsWithStatusPaused();

  /** Retrieves filter metadata for a tenant. */
  Maybe<FilterResponse> findFilters(String tenantId);

  /** Lists scheduled CTAs with generation ids (for activation job). */
  Single<Map<Long, ScheduledCTA>> findAllWithStatusScheduled();

  /** Updates status and time bounds. */
  Completable update(Long id, CTAStatus status, Long startTime, Long endTime);

  /** Updates status only. */
  Completable update(Long id, CTAStatus status);

  /** Updates status with generation check. */
  Completable update(Long id, int generation, CTAStatus status);

  /** Replaces behaviour tags for a CTA. */
  Completable update(Long id, List<String> behaviourTag);

  /** Updates full CTA with generation check. */
  Completable update(CTA cta, int generation);

  /** Terminates or concludes CTA updating time bounds accordingly. */
  Completable terminateOrConclude(Long id, CTAStatus status, Long startTime, Long endTime);

  /** Terminates or concludes CTA when only end time is applicable. */
  Completable terminateOrConclude(Long id, CTAStatus status, Long endTime);

  /** Updates filter metadata (tags, team, names). */
  Completable updateFilters(
      String tenantId, List<String> tags, String team, String name, String createdBy);
}
