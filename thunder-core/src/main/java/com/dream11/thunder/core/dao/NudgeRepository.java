package com.dream11.thunder.core.dao;

import com.dream11.thunder.core.model.Nudge;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;

public interface NudgeRepository {

  Maybe<Nudge> find(String tenantId, String id);

  Single<Map<String, Nudge>> findAll();

  Completable create(String tenantId, Nudge template);

  Completable update(String tenantId, Nudge template);
}

