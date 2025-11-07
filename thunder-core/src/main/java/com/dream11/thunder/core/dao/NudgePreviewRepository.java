package com.dream11.thunder.core.dao;

import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public interface NudgePreviewRepository {
  Completable createOrUpdate(String tenantId, NudgePreview nudgePreview);

  Maybe<NudgePreview> find(String tenantId, String id);
}

