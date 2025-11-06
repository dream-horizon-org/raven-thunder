package com.dream11.thunder.core.dao;

import com.dream11.thunder.core.model.BehaviourTag;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;

public interface BehaviourTagsRepository {

  Single<Map<String, BehaviourTag>> findAll();

  Single<Map<String, BehaviourTag>> findAll(String tenantId);

  Maybe<BehaviourTag> find(String tenantId, String behaviourTagName);

  Completable create(String tenantId, BehaviourTag behaviourTag);

  Completable update(String tenantId, String behaviourTagName, BehaviourTag behaviourTag);
}

