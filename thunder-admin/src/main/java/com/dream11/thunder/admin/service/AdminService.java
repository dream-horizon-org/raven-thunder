package com.dream11.thunder.admin.service;

import com.dream11.thunder.admin.io.request.CTARequest;
import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.admin.io.response.CTAListResponse;
import com.dream11.thunder.admin.model.FilterProps;
import com.dream11.thunder.core.io.response.FilterResponse;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AdminService {
  Single<Long> createCTA(String tenantId, @NotNull @Valid CTARequest cta, @NotNull String user);

  Completable updateCTA(
      String tenantId, @NotNull @Valid CTAUpdateRequest cta, Long ctaId, String user);

  Single<CTA> fetchCTA(String tenantId, Long ctaId);

  Single<FilterResponse> fetchFilters(String tenantId);

  Single<CTAListResponse> fetchCTAs(
      String tenantId, FilterProps filterProps, int pageNumber, int pageSize);

  Completable updateStatusToPaused(String tenantId, Long ctaId);

  Completable updateStatusToLive(String tenantId, Long ctaId);

  Completable updateStatusToConcluded(String tenantId, Long ctaId);

  Completable updateStatusToScheduled(String tenantId, Long ctaId);

  Completable updateStatusToTerminated(String tenantId, Long ctaId);

  Completable createNudge(String tenantId, Nudge template);

  Completable updateNudge(String tenantId, Nudge template);

  Completable createOrUpdateNudgePreview(String tenantId, NudgePreview nudgePreview);

  Single<FilterResponse> findFilters(String tenantId);

  void activateScheduledCTA();

  void terminateExpiredCTA();
}

