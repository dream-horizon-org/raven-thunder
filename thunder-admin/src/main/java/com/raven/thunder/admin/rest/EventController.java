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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Tag(
    name = "Events",
    description =
        "APIs for managing Events. Events define the structure and properties of user actions "
            + "that can trigger CTAs. Events include event names and their associated properties with types, "
            + "expected values, and mandatory flags.")
@Path("/thunder")
public class EventController {

  private final EventService service;

  @Inject
  public EventController(EventService service) {
    this.service = service;
  }

  @Tag(name = "Events")
  @Operation(
      summary = "Create or Update Events",
      description =
          "Creates new events or updates existing events. Events define the structure and properties "
              + "of user actions that can trigger CTAs. Each event includes a name and a list of properties "
              + "with types, expected values, and mandatory flags.",
      operationId = "upsertEvents")
  @APIResponse(
      responseCode = "200",
      description = "Events created or updated successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Events upserted successfully",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": {\n"
                            + "    \"eventsProcessed\": 2\n"
                            + "  },\n"
                            + "  \"statusCode\": 200\n"
                            + "}")
              }))
  @APIResponse(responseCode = "400", description = "Invalid request data or validation failed")
  @POST
  @Path("/events")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventsUpsertResponse>> upsertEvents(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant ID for multi-tenancy support",
              required = false,
              schema = @Schema(defaultValue = "default"))
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @Parameter(
              name = "x-source",
              description = "Source system identifier",
              required = false,
              schema = @Schema(defaultValue = "CONCORD"))
          @DefaultValue("CONCORD")
          @HeaderParam("x-source")
          String source,
      @RequestBody(
              description = "List of events to create or update",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = EventsUpsertRequest.class)))
          @NotNull
          @Valid
          EventsUpsertRequest request) {
    return ResponseWrapper.fromSingle(
        service
            .upsertEvents(tenantId, source, request)
            .map(count -> EventsUpsertResponse.builder().eventsProcessed(count).build()),
        200);
  }

  @Tag(name = "Events")
  @Operation(
      summary = "Get All Events",
      description =
          "Retrieves a list of all events for the specified tenant. Each event includes its name "
              + "and associated properties with types, expected values, and mandatory flags.",
      operationId = "getAllEvents")
  @APIResponse(
      responseCode = "200",
      description = "List of events retrieved successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Events list",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": {\n"
                            + "    \"eventList\": [\n"
                            + "      {\n"
                            + "        \"eventName\": \"UserLoginEvent\",\n"
                            + "        \"properties\": [\n"
                            + "          {\n"
                            + "            \"propertyName\": \"userId\",\n"
                            + "            \"type\": \"String\",\n"
                            + "            \"isMandatory\": true\n"
                            + "          }\n"
                            + "        ]\n"
                            + "      }\n"
                            + "    ]\n"
                            + "  },\n"
                            + "  \"statusCode\": 200\n"
                            + "}")
              }))
  @GET
  @Path("/events")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventsListResponse>> getAllEvents(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant ID for multi-tenancy support",
              required = false,
              schema = @Schema(defaultValue = "default"))
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId) {
    return ResponseWrapper.fromSingle(service.getAllEvents(tenantId), 200);
  }

  @Tag(name = "Events")
  @Operation(
      summary = "Get All Event Names",
      description =
          "Retrieves a list of all event names for the specified tenant. This is a lightweight endpoint "
              + "that returns only the event names without their property details.",
      operationId = "getAllEventNames")
  @APIResponse(
      responseCode = "200",
      description = "List of event names retrieved successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Event names list",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": {\n"
                            + "    \"eventNames\": [\"UserLoginEvent\", \"PurchaseCompletedEvent\"]\n"
                            + "  },\n"
                            + "  \"statusCode\": 200\n"
                            + "}")
              }))
  @GET
  @Path("/events/list/names")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventNamesResponse>> getAllEventNames(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant ID for multi-tenancy support",
              required = false,
              schema = @Schema(defaultValue = "default"))
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId) {
    return ResponseWrapper.fromSingle(service.getAllEventNames(tenantId), 200);
  }

  @Tag(name = "Events")
  @Operation(
      summary = "Get Event by Name",
      description =
          "Retrieves a specific event by its name for the specified tenant. Returns the event name "
              + "and all associated properties with types, expected values, and mandatory flags.",
      operationId = "getEvent")
  @APIResponse(
      responseCode = "200",
      description = "Event retrieved successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Event details",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": {\n"
                            + "    \"eventName\": \"UserLoginEvent\",\n"
                            + "    \"properties\": [\n"
                            + "      {\n"
                            + "        \"propertyName\": \"userId\",\n"
                            + "        \"type\": \"String\",\n"
                            + "        \"isMandatory\": true\n"
                            + "      }\n"
                            + "    ]\n"
                            + "  },\n"
                            + "  \"statusCode\": 200\n"
                            + "}")
              }))
  @APIResponse(responseCode = "404", description = "Event not found")
  @GET
  @Path("/events/{event-name}")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<EventResponse>> getEvent(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant ID for multi-tenancy support",
              required = false,
              schema = @Schema(defaultValue = "default"))
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @Parameter(
              name = "event-name",
              description = "Name of the event to retrieve",
              required = true)
          @NotNull
          @PathParam("event-name")
          String eventName) {
    return ResponseWrapper.fromSingle(service.getEvent(tenantId, eventName), 200);
  }

  @Tag(name = "Events")
  @Operation(
      summary = "Update Event Properties",
      description =
          "Updates the properties of an existing event. Only the properties provided in the request "
              + "will be updated. This allows partial updates to event property definitions.",
      operationId = "patchEvent")
  @APIResponse(
      responseCode = "200",
      description = "Event properties updated successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Event updated",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": null,\n"
                            + "  \"statusCode\": 200\n"
                            + "}")
              }))
  @APIResponse(responseCode = "400", description = "Invalid request data or validation failed")
  @APIResponse(responseCode = "404", description = "Event not found")
  @PUT
  @Path("/events/{event-name}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> patchEvent(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant ID for multi-tenancy support",
              required = false,
              schema = @Schema(defaultValue = "default"))
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @Parameter(
              name = "event-name",
              description = "Name of the event to update",
              required = true)
          @NotNull
          @PathParam("event-name")
          String eventName,
      @RequestBody(
              description = "List of properties to update for the event",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = EventPatchRequest.class)))
          @NotNull
          @Valid
          EventPatchRequest request) {
    return ResponseWrapper.fromCompletable(
        service.patchEvent(tenantId, eventName, request), null, 200);
  }

  @Tag(name = "Events")
  @Operation(
      summary = "Delete Event",
      description =
          "Deletes an event by its name for the specified tenant. This operation is irreversible.",
      operationId = "deleteEvent")
  @APIResponse(
      responseCode = "200",
      description = "Event deleted successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Event deleted",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": null,\n"
                            + "  \"statusCode\": 200\n"
                            + "}")
              }))
  @APIResponse(responseCode = "404", description = "Event not found")
  @DELETE
  @Path("/events/{event-name}")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> deleteEvent(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant ID for multi-tenancy support",
              required = false,
              schema = @Schema(defaultValue = "default"))
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @Parameter(
              name = "event-name",
              description = "Name of the event to delete",
              required = true)
          @NotNull
          @PathParam("event-name")
          String eventName) {
    return ResponseWrapper.fromCompletable(service.deleteEvent(tenantId, eventName), null, 200);
  }
}
