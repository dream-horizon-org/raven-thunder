package com.dream11.thunder.api.service;

import com.dream11.thunder.api.io.request.CTASnapshotRequest;
import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface SdkService {

  /**
   * Resolves eligible CTAs for a user on app launch, merging with user's snapshot. Optionally
   * merges a delta snapshot before computing the response.
   *
   * @param tenantId tenant identifier
   * @param userId user identifier
   * @param deltaSnapshot optional delta snapshot from client
   * @return CTAResponse or empty if no eligible CTAs
   */
  Maybe<CTAResponse> appLaunch(String tenantId, Long userId, CTASnapshotRequest deltaSnapshot);

  /**
   * Merges a delta snapshot into the stored snapshot for a user.
   *
   * @param tenantId tenant identifier
   * @param userId user identifier
   * @param deltaSnapshot delta snapshot
   * @return true when merge is persisted
   */
  Single<Boolean> merge(String tenantId, Long userId, CTASnapshotRequest deltaSnapshot);

  /** Finds a nudge preview by id for the tenant. */
  Maybe<NudgePreview> findNudgePreview(String tenantId, String id);
}
