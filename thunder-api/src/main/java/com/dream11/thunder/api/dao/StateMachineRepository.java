package com.dream11.thunder.api.dao;

import com.dream11.thunder.api.model.UserDataSnapshot;
import io.reactivex.rxjava3.Maybe;
import io.reactivex.rxjava3.Single;

public interface StateMachineRepository {

  Maybe<UserDataSnapshot> find(String tenantId, Long userId);

  Single<Boolean> upsert(String tenantId, Long userId, UserDataSnapshot snapshot);
}
