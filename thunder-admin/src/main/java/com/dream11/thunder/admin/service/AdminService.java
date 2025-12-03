package com.dream11.thunder.admin.service;

import com.dream11.thunder.admin.io.request.CTARequest;
import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.admin.io.response.CTAListResponse;
import com.dream11.thunder.admin.model.FilterProps;
import com.dream11.thunder.core.io.response.FilterResponse;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AdminService {
  /**
   * Creates a new CTA and updates filter metadata (tags, team, names).
   *
   * @param tenantId tenant identifier
   * @param cta validated CTA create request
   * @param user user performing the operation
   * @return generated CTA id
   */
  Single<Long> createCTA(String tenantId, @NotNull @Valid CTARequest cta, @NotNull String user);

  /**
   * Updates an existing CTA using optimistic concurrency (generation id). Also updates filter
   * metadata.
   *
   * @param tenantId tenant identifier
   * @param cta validated CTA update request
   * @param ctaId CTA identifier
   * @param user user performing the operation
   * @return completes when update succeeds
   */
  Completable updateCTA(
      String tenantId, @NotNull @Valid CTAUpdateRequest cta, Long ctaId, String user);

  /**
   * Fetches a CTA by id for a tenant.
   *
   * @param tenantId tenant identifier
   * @param ctaId CTA identifier
   * @return CTA if found, or error if not found
   */
  Single<CTA> fetchCTA(String tenantId, Long ctaId);

  /**
   * Retrieves CTA filter metadata (tags, teams, names) for a tenant.
   *
   * @param tenantId tenant identifier
   */
  Single<FilterResponse> fetchFilters(String tenantId);

  /**
   * Lists CTAs with filtering and pagination.
   *
   * @param tenantId tenant identifier
   * @param filterProps filter criteria (name, team, tags, status, etc.)
   * @param pageNumber 0-indexed page number
   * @param pageSize items per page
   * @return paginated CTA list along with status-wise counts
   */
  Single<CTAListResponse> fetchCTAs(
      String tenantId, FilterProps filterProps, int pageNumber, int pageSize);

  /** Transitions CTA status to PAUSED from LIVE or SCHEDULED. */
  Completable updateStatusToPaused(String tenantId, Long ctaId);

  /** Transitions CTA status to LIVE from DRAFT or PAUSED, setting start/end times as needed. */
  Completable updateStatusToLive(String tenantId, Long ctaId);

  /** Transitions CTA status to CONCLUDED from LIVE or PAUSED. */
  Completable updateStatusToConcluded(String tenantId, Long ctaId);

  /** Transitions CTA status to SCHEDULED from DRAFT or PAUSED if start time is in the future. */
  Completable updateStatusToScheduled(String tenantId, Long ctaId);

  /** Transitions CTA status to TERMINATED from DRAFT, LIVE or PAUSED. */
  Completable updateStatusToTerminated(String tenantId, Long ctaId);

  /** Creates or updates a nudge preview for the given tenant. */
  Completable createOrUpdateNudgePreview(String tenantId, NudgePreview nudgePreview);

  /** Retrieves CTA filter metadata (tags, teams, names) for a tenant. */
  Single<FilterResponse> findFilters(String tenantId);

  /** Activates scheduled CTAs whose start time has passed. */
  void activateScheduledCTA();

  /** Concludes CTAs whose end time has passed. */
  void terminateExpiredCTA();
}
