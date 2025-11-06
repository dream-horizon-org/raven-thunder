package com.dream11.thunder.api.rest;

import com.dream11.thunder.core.io.Response;
// Removed Dream11 RestResponse dependency;
import com.dream11.thunder.api.model.UserDataSnapshot;
import com.dream11.thunder.api.service.AppDebugService;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.util.ResponseWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import com.google.inject.Inject;
import javax.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/cta")
public class AppDebugController {

  private final AppDebugService service;
  Map<String, Nudge> emptyResponse = new HashMap<>();

  @Inject
  public AppDebugController(AppDebugService service) {
    this.service = service;
  }

  @GET
  @Path("/rules/active")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Map<Long, CTA>>> findAllActiveCTA(
      @DefaultValue("dream11") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @QueryParam("cache") Boolean cache) {
    return ResponseWrapper.fromSingle(service.findAllActiveCTA(tenantId, cache), 200);
  }

  @GET
  @Path("/rules/{id}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTA>> findCTA(
      @DefaultValue("dream11") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("id") String id) {
    return ResponseWrapper.fromMaybe(service.findCTA(tenantId, Long.parseLong(id)), null, 200);
  }

  @GET
  @Path("/state-machines")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<UserDataSnapshot>> find(
      @DefaultValue("dream11") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @HeaderParam("auth-userid") Long userId) {
    return ResponseWrapper.fromMaybe(service.findStateMachine(tenantId, userId), null, 200);
  }
}
