package com.raven.thunder.admin.service.event;

import com.google.inject.Inject;
import com.raven.thunder.admin.constant.Constants;
import com.raven.thunder.admin.io.request.EventInput;
import com.raven.thunder.admin.io.request.EventPatchRequest;
import com.raven.thunder.admin.io.request.EventsUpsertRequest;
import com.raven.thunder.admin.io.response.EventNamesResponse;
import com.raven.thunder.admin.io.response.EventResponse;
import com.raven.thunder.admin.io.response.EventsListResponse;
import com.raven.thunder.admin.service.EventService;
import com.raven.thunder.core.dao.EventRepository;
import com.raven.thunder.core.model.EventData;
import com.raven.thunder.core.model.EventProperty;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;

  @Inject
  public EventServiceImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @Override
  public Completable upsertEvent(String tenantId, String source, EventInput eventInput) {
    EventData eventData = convertToEventData(tenantId, source, eventInput);
    return eventRepository
        .upsert(tenantId, eventInput.getEventName(), eventData)
        .doOnComplete(
            () ->
                log.debug(
                    "Successfully upserted event: {} for tenant: {} with source: {}",
                    eventInput.getEventName(),
                    tenantId,
                    source))
        .doOnError(
            error ->
                log.error(
                    "Error upserting event: {} for tenant: {} with source: {}",
                    eventInput.getEventName(),
                    tenantId,
                    source,
                    error));
  }

  @Override
  public Single<Integer> upsertEvents(String tenantId, String source, EventsUpsertRequest request) {
    int eventCount = request.getEvents().size();
    return Observable.fromIterable(request.getEvents())
        .flatMapCompletable(eventInput -> upsertEvent(tenantId, source, eventInput))
        .doOnSubscribe(
            ignored ->
                log.info(
                    "Starting upsert of {} events for tenant: {} with source: {}",
                    eventCount,
                    tenantId,
                    source))
        .doOnComplete(
            () ->
                log.info(
                    "Successfully upserted {} events for tenant: {} with source: {}",
                    eventCount,
                    tenantId,
                    source))
        .doOnError(
            error ->
                log.error(
                    "Error upserting events for tenant: {} with source: {}",
                    tenantId,
                    source,
                    error))
        .toSingleDefault(eventCount);
  }

  private EventData convertToEventData(String tenantId, String source, EventInput eventInput) {
    List<EventProperty> properties =
        eventInput.getProperties().stream()
            .map(
                prop ->
                    EventProperty.builder()
                        .propertyName(prop.getPropertyName())
                        .type(prop.getType())
                        .expectedValue(prop.getExpectedValue())
                        .isMandatory(prop.getIsMandatory())
                        .description(prop.getDescription())
                        .build())
            .collect(Collectors.toList());

    return EventData.builder()
        .tenantId(tenantId)
        .eventName(eventInput.getEventName())
        .source(source)
        .lastSyncedAt(System.currentTimeMillis() / 1000) // Convert to epoch seconds
        .properties(properties)
        .build();
  }

  @Override
  public Single<EventsListResponse> getAllEvents(String tenantId) {
    return eventRepository
        .findAllByTenant(tenantId)
        .map(
            eventDataList -> {
              List<EventResponse> eventResponses =
                  eventDataList.stream()
                      .map(
                          eventData ->
                              EventResponse.builder()
                                  .eventName(eventData.getEventName())
                                  .properties(eventData.getProperties())
                                  .build())
                      .collect(Collectors.toList());

              EventsListResponse eventsListResponse =
                  EventsListResponse.builder().eventList(eventResponses).build();

              return eventsListResponse;
            })
        .doOnSubscribe(ignored -> log.info("Fetching all events for tenant: {}", tenantId))
        .doOnSuccess(
            response ->
                log.info(
                    "Successfully fetched {} events for tenant: {}",
                    response.getEventList().size(),
                    tenantId))
        .doOnError(error -> log.error("Error fetching events for tenant: {}", tenantId, error));
  }

  @Override
  public Single<EventResponse> getEvent(String tenantId, String eventName) {
    return eventRepository
        .findByTenantAndEventName(tenantId, eventName)
        .map(
            eventData ->
                EventResponse.builder()
                    .eventName(eventData.getEventName())
                    .properties(eventData.getProperties())
                    .build())
        .switchIfEmpty(
            Single.defer(
                () ->
                    Single.error(
                        new RuntimeException(
                            "no Event exists for given event name")))) // TODO: Use proper exception
        .doOnSubscribe(
            ignored -> log.info("Fetching event: {} for tenant: {}", eventName, tenantId))
        .doOnSuccess(
            response ->
                log.info("Successfully fetched event: {} for tenant: {}", eventName, tenantId))
        .doOnError(
            error ->
                log.error("Error fetching event: {} for tenant: {}", eventName, tenantId, error));
  }

  @Override
  public Completable deleteEvent(String tenantId, String eventName) {
    return eventRepository
        .delete(tenantId, eventName)
        .flatMapCompletable(
            existed -> {
              if (!existed) {
                return Completable.error(
                    new RuntimeException(
                        "no Event exists for given event name")); // TODO: Use proper exception
              }
              return Completable.complete();
            })
        .doOnSubscribe(
            ignored -> log.info("Deleting event: {} for tenant: {}", eventName, tenantId))
        .doOnComplete(
            () -> log.info("Successfully deleted event: {} for tenant: {}", eventName, tenantId))
        .doOnError(
            error ->
                log.error("Error deleting event: {} for tenant: {}", eventName, tenantId, error));
  }

  @Override
  public Completable patchEvent(String tenantId, String eventName, EventPatchRequest request) {
    // Convert new properties from request
    List<EventProperty> updatedProperties =
        request.getProperties().stream()
            .map(
                prop ->
                    EventProperty.builder()
                        .propertyName(prop.getPropertyName())
                        .type(prop.getType())
                        .expectedValue(prop.getExpectedValue())
                        .isMandatory(prop.getIsMandatory())
                        .description(prop.getDescription())
                        .build())
            .collect(Collectors.toList());

    // Create EventData with new properties - full overwrite
    EventData updatedEventData =
        EventData.builder()
            .tenantId(tenantId)
            .eventName(eventName)
            .source(Constants.PUT_SOURCE) // Default source for PUT operations
            .lastSyncedAt(System.currentTimeMillis() / 1000) // Update sync time
            .properties(updatedProperties) // Full overwrite of properties
            .build();

    // Upsert directly - will create if doesn't exist, update if exists
    return eventRepository
        .upsert(tenantId, eventName, updatedEventData)
        .doOnSubscribe(
            ignored -> log.info("Patching event: {} for tenant: {}", eventName, tenantId))
        .doOnComplete(
            () -> log.info("Successfully patched event: {} for tenant: {}", eventName, tenantId))
        .doOnError(
            error ->
                log.error("Error patching event: {} for tenant: {}", eventName, tenantId, error));
  }

  @Override
  public Single<EventNamesResponse> getAllEventNames(String tenantId) {
    return eventRepository
        .findAllEventNamesByTenant(tenantId)
        .map(eventNames -> EventNamesResponse.builder().eventNames(eventNames).build())
        .doOnSubscribe(ignored -> log.info("Fetching all event names for tenant: {}", tenantId))
        .doOnSuccess(
            response ->
                log.info(
                    "Successfully fetched {} event names for tenant: {}",
                    response.getEventNames().size(),
                    tenantId))
        .doOnError(
            error -> log.error("Error fetching event names for tenant: {}", tenantId, error));
  }
}
