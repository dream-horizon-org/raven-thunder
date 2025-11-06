package com.dream11.thunder.api.service;

import com.dream11.thunder.api.model.UserDataSnapshot;
import com.dream11.thunder.core.model.CTA;
import io.reactivex.rxjava3.Maybe;
import io.reactivex.rxjava3.Single;
import java.util.Map;

public interface AppDebugService {

  Single<Map<Long, CTA>> findAllActiveCTA(String tenantId, Boolean cache);

  Maybe<CTA> findCTA(String tenantId, Long id);

  Maybe<UserDataSnapshot> findStateMachine(String tenantId, Long userId);
}
