package com.dream11.thunder.admin.rest;

import static com.dream11.thunder.admin.constant.Constants.USER_ID_NULL_ERROR_MESSAGE;

import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.admin.io.response.BehaviourTagsResponse;
import com.dream11.thunder.admin.service.BehaviourTagService;
import com.dream11.thunder.core.io.Response;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.util.ResponseWrapper;
import com.google.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/** REST controller for behaviour tag management (list, detail, create, update). */
@Slf4j
@Path("/thunder")
public class BehaviourTagController {

  private final BehaviourTagService service;

  @Inject
  public BehaviourTagController(BehaviourTagService service) {
    this.service = service;
  }

  @GET
  @Path("/behaviour-tags/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<BehaviourTagsResponse>> getBehaviourTags(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromSingle(service.fetchAllBehaviourTags(tenantId), 200);
  }

  @GET
  @Path("/behaviour-tags/{behaviourTagName}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<BehaviourTag>> getBehaviourTags(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @PathParam("behaviourTagName") String behaviourTagName) {
    return ResponseWrapper.fromSingle(
        service.fetchBehaviourTagDetail(tenantId, behaviourTagName), 200);
  }

  @POST
  @Path("/behaviour-tags/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> createBehaviour(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @Valid BehaviourTagCreateRequest behaviourTagCreateRequest,
      @NotNull(message = USER_ID_NULL_ERROR_MESSAGE) @HeaderParam("user") String userId) {
    behaviourTagCreateRequest.validate();
    return ResponseWrapper.fromCompletable(
        service.createBehaviourTag(tenantId, userId, behaviourTagCreateRequest), null, 200);
  }

  @PUT
  @Path("/behaviour-tags/{behaviourTagName}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateBehaviourTag(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @Valid BehaviourTagPutRequest behaviourTagPutRequest,
      @NotNull @PathParam("behaviourTagName") String behaviourTagName,
      @NotNull(message = USER_ID_NULL_ERROR_MESSAGE) @HeaderParam("user") String userId) {
    behaviourTagPutRequest.validate(behaviourTagName);
    return ResponseWrapper.fromCompletable(
        service.updateBehaviourTag(tenantId, behaviourTagName, behaviourTagPutRequest, userId),
        null,
        200);
  }
}
