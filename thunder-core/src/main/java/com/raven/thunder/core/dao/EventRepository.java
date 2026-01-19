package com.raven.thunder.core.dao;

import com.raven.thunder.core.model.EventData;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

public interface EventRepository {
  Completable upsert(String tenantId, String eventName, EventData eventData);

  Single<List<EventData>> findAllByTenant(String tenantId);

  Maybe<EventData> findByTenantAndEventName(String tenantId, String eventName);

  Single<List<String>> findAllEventNamesByTenant(String tenantId);

  Single<Boolean> delete(String tenantId, String eventName);
}
