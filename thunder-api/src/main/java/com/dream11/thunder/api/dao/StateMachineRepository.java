package com.dream11.thunder.api.dao;

import com.dream11.thunder.api.model.UserDataSnapshot;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/** Repository for user state machine snapshots. */
public interface StateMachineRepository {

  /** Finds the user snapshot for a tenant. */
  Maybe<UserDataSnapshot> find(String tenantId, Long userId);

  /** Upserts the user snapshot for a tenant. */
  Single<Boolean> upsert(String tenantId, Long userId, UserDataSnapshot snapshot);
}
