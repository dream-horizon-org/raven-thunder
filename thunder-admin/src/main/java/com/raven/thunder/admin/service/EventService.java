package com.raven.thunder.admin.service;

import com.raven.thunder.admin.io.request.EventPatchRequest;
import com.raven.thunder.admin.io.request.EventsUpsertRequest;
import com.raven.thunder.admin.io.response.EventNamesResponse;
import com.raven.thunder.admin.io.response.EventResponse;
import com.raven.thunder.admin.io.response.EventsListResponse;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface EventService {
  Completable upsertEvent(
      String tenantId, String source, com.raven.thunder.admin.io.request.EventInput eventInput);

  Single<Integer> upsertEvents(String tenantId, String source, EventsUpsertRequest request);

  Single<EventsListResponse> getAllEvents(String tenantId);

  Single<EventResponse> getEvent(String tenantId, String eventName);

  Completable patchEvent(String tenantId, String eventName, EventPatchRequest request);

  Completable deleteEvent(String tenantId, String eventName);

  Single<EventNamesResponse> getAllEventNames(String tenantId);
}
