package com.dream11.thunder.api.service;

import com.dream11.thunder.api.io.request.CTASnapshotRequest;
import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface SdkService {

  @Deprecated
  Maybe<Nudge> findNudge(String id);

  Maybe<CTAResponse> appLaunch(String tenantId, Long userId, CTASnapshotRequest deltaSnapshot);

  Single<Boolean> merge(String tenantId, Long userId, CTASnapshotRequest deltaSnapshot);

  Maybe<NudgePreview> findNudgePreview(String tenantId, String id);
}
