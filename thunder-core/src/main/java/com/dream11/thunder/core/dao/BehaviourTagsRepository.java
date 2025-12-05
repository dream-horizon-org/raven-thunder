package com.dream11.thunder.core.dao;

import com.dream11.thunder.core.model.BehaviourTag;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;

/** Repository for behaviour tag CRUD and lookups. */
public interface BehaviourTagsRepository {

  /** Lists all behaviour tags across tenants (for caching). */
  Single<Map<String, BehaviourTag>> findAll();

  /** Lists all behaviour tags for a tenant. */
  Single<Map<String, BehaviourTag>> findAll(String tenantId);

  /** Finds a behaviour tag by name for a tenant. */
  Maybe<BehaviourTag> find(String tenantId, String behaviourTagName);

  /** Creates a behaviour tag. */
  Completable create(String tenantId, BehaviourTag behaviourTag);

  /** Updates a behaviour tag. */
  Completable update(String tenantId, String behaviourTagName, BehaviourTag behaviourTag);
}
