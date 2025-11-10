package com.dream11.thunder.api.rest;

import com.dream11.thunder.core.io.Response;
// Removed Dream11 RestResponse dependency;
import com.dream11.thunder.api.io.request.CTASnapshotRequest;
import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.api.service.SdkService;
import com.dream11.thunder.core.model.NudgePreview;
import com.dream11.thunder.core.util.ResponseWrapper;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.CompletionStage;
import javax.annotation.Nullable;
import com.google.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/cta")
public class SdkApiController {

  private final SdkService service;

  private final CTAResponse emptyCTAResponse = new CTAResponse();

  @Inject
  public SdkApiController(SdkService service) {
    this.service = service;
  }

  @GET
  @Path("/nudge/preview/{id}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<NudgePreview>> getNudgePreview(
      @PathParam("id") String id,
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromMaybe(service.findNudgePreview(tenantId, id), null, 200);
  }

  @POST
  @Path("/active/state-machines/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTAResponse>> appLaunch(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull(message = "auth-userid cannot be null") @HeaderParam("auth-userid") Long userId,
      @Nullable @HeaderParam("app_version") String appVersion,
      @Nullable @HeaderParam("codepush_version") String cpVersion,
      @Nullable @HeaderParam("package_name") String packageName,
      @Nullable @HeaderParam("api_version") Long apiVersion,
      @Valid CTASnapshotRequest deltaSnapshot) {
    if (apiVersion == null || apiVersion < 1)
      return ResponseWrapper.fromMaybe(Maybe.just(emptyCTAResponse), emptyCTAResponse, 200);
    return ResponseWrapper.fromMaybe(
        service.appLaunch(tenantId, userId, deltaSnapshot), emptyCTAResponse, 200);
  }

  @POST
  @Path("/v1/active/state-machines/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTAResponse>> appLaunchV1(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull(message = "auth-userid cannot be null") @HeaderParam("auth-userid") Long userId,
      @Nullable @HeaderParam("app_version") String appVersion,
      @Nullable @HeaderParam("codepush_version") String cpVersion,
      @Nullable @HeaderParam("package_name") String packageName,
      @Nullable @HeaderParam("api_version") Long apiVersion,
      @Valid CTASnapshotRequest deltaSnapshot) {
    // v1 endpoint is just an alias for the main endpoint
    return appLaunch(tenantId, userId, appVersion, cpVersion, packageName, apiVersion, deltaSnapshot);
  }

  @POST
  @Path("/state-machines/snapshot/delta/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Boolean>> merge(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull(message = "auth-userid cannot be null") @HeaderParam("auth-userid") Long userId,
      @Nullable @HeaderParam("app_version") String appVersion,
      @Nullable @HeaderParam("codepush_version") String cpVersion,
      @Nullable @HeaderParam("package_name") String packageName,
      @Nullable @HeaderParam("api_version") Long apiVersion,
      @Valid CTASnapshotRequest deltaSnapshot) {
    if (apiVersion == null || apiVersion < 1)
      return ResponseWrapper.fromSingle(Single.just(false), 200);
    return ResponseWrapper.fromSingle(service.merge(tenantId, userId, deltaSnapshot), 200);
  }
}
