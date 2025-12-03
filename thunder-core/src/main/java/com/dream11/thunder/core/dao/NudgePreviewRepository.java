package com.dream11.thunder.core.dao;

import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

/** Repository for Nudge preview CRUD. */
public interface NudgePreviewRepository {
  /** Creates or updates a nudge preview for the tenant. */
  Completable createOrUpdate(String tenantId, NudgePreview nudgePreview);

  /** Finds a nudge preview by id for the tenant. */
  Maybe<NudgePreview> find(String tenantId, String id);
}
