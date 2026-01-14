package com.raven.thunder.admin.rest;

import com.google.inject.Inject;
import com.raven.thunder.admin.io.request.EventPatchRequest;
import com.raven.thunder.admin.io.request.EventsUpsertRequest;
import com.raven.thunder.admin.io.response.EventNamesResponse;
import com.raven.thunder.admin.io.response.EventResponse;
import com.raven.thunder.admin.io.response.EventsListResponse;
import com.raven.thunder.admin.io.response.EventsUpsertResponse;
import com.raven.thunder.admin.service.EventService;
import com.raven.thunder.core.io.Response;
import com.raven.thunder.core.util.ResponseWrapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/thunder")
public class EventController {

  private final EventService service;

  @Inject
  public EventController(EventService service) {
    this.service = service;
  }

  @POST
  @Path("/events")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventsUpsertResponse>> upsertEvents(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @DefaultValue("CONCORD") @HeaderParam("x-source") String source,
      @NotNull @Valid EventsUpsertRequest request) {
    return ResponseWrapper.fromSingle(
        service
            .upsertEvents(tenantId, source, request)
            .map(count -> EventsUpsertResponse.builder().eventsProcessed(count).build()),
        200);
  }

  @GET
  @Path("/events")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventsListResponse>> getAllEvents(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromSingle(service.getAllEvents(tenantId), 200);
  }

  @GET
  @Path("/events/list/names")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventNamesResponse>> getAllEventNames(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromSingle(service.getAllEventNames(tenantId), 200);
  }

  @GET
  @Path("/events/{event-name}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventResponse>> getEvent(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("event-name") String eventName) {
    return ResponseWrapper.fromSingle(service.getEvent(tenantId, eventName), 200);
  }

  @PUT
  @Path("/events/{event-name}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> patchEvent(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("event-name") String eventName,
      @NotNull @Valid EventPatchRequest request) {
    return ResponseWrapper.fromCompletable(
        service.patchEvent(tenantId, eventName, request), null, 200);
  }

  @DELETE
  @Path("/events/{event-name}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> deleteEvent(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("event-name") String eventName) {
    return ResponseWrapper.fromCompletable(service.deleteEvent(tenantId, eventName), null, 200);
  }
}
