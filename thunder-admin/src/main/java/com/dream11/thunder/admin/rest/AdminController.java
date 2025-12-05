package com.dream11.thunder.admin.rest;

import static com.dream11.thunder.admin.constant.Constants.USER_ID_NULL_ERROR_MESSAGE;

import com.dream11.thunder.admin.io.request.CTARequest;
import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.admin.io.response.CTAListResponse;
import com.dream11.thunder.admin.model.FilterProps;
import com.dream11.thunder.admin.service.AdminService;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.io.Response;
import com.dream11.thunder.core.io.response.FilterResponse;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.NudgePreview;
import com.dream11.thunder.core.util.FormatUtil;
import com.dream11.thunder.core.util.ResponseWrapper;
import com.google.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Admin REST controller for managing CTAs, filters and nudge previews. Exposes endpoints under
 * "/thunder" for create/update/list/status transitions.
 */
@Slf4j
@Path("/thunder")
public class AdminController {

  private final AdminService service;
  private final NudgePreviewRepository nudgePreviewRepository;

  @Inject
  public AdminController(AdminService service, NudgePreviewRepository nudgePreviewRepository) {
    this.service = service;
    this.nudgePreviewRepository = nudgePreviewRepository;
  }

  @POST
  @Path("/nudge/preview/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> createOrUpdateNudgePreview(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @Valid NudgePreview nudgePreview) {
    nudgePreview.validate();
    return ResponseWrapper.fromCompletable(
        service.createOrUpdateNudgePreview(tenantId, nudgePreview), null, 200);
  }

  @GET
  @Path("/nudge/preview/")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<NudgePreview>> getNudgePreview(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @QueryParam("id") String id) {
    return ResponseWrapper.fromSingle(nudgePreviewRepository.find(tenantId, id).toSingle(), 200);
  }

  @POST
  @Path("/ctas/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Long>> createCta(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @Valid CTARequest cta,
      @NotNull(message = USER_ID_NULL_ERROR_MESSAGE) @HeaderParam("user") String user) {
    cta.validate();
    return ResponseWrapper.fromSingle(service.createCTA(tenantId, cta, user), 200);
  }

  @PUT
  @Path("/ctas/{ctaId}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateCta(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @Valid CTAUpdateRequest cta,
      @NotNull @PathParam("ctaId") Long ctaId,
      @NotNull(message = USER_ID_NULL_ERROR_MESSAGE) @HeaderParam("user") String user) {
    cta.validate();
    return ResponseWrapper.fromCompletable(
        service.updateCTA(tenantId, cta, ctaId, user), null, 200);
  }

  @GET
  @Path("/ctas/{ctaId}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTA>> getCTA(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("ctaId") Long ctaId) {
    return ResponseWrapper.fromSingle(service.fetchCTA(tenantId, ctaId), 200);
  }

  @GET
  @Path("/ctas/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTAListResponse>> getCTAs(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @QueryParam("name") String name,
      @QueryParam("teams") String teams,
      @QueryParam("tags") String tags,
      @QueryParam("status") String status,
      @QueryParam("behaviourTag") String behaviourTag,
      @QueryParam("createdBy") String createdBy,
      @QueryParam("searchName") String searchName,
      @QueryParam("pageNumber") Integer pageNumber,
      @QueryParam("pageSize") Integer pageSize) {
    return ResponseWrapper.fromSingle(
        service.fetchCTAs(
            tenantId,
            new FilterProps(
                name,
                searchName,
                status,
                createdBy,
                FormatUtil.extractList(tags),
                FormatUtil.extractList(teams),
                behaviourTag),
            pageNumber == null ? 0 : pageNumber,
            pageSize == null ? 10 : pageSize),
        200);
  }

  @GET
  @Path("/filters/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<FilterResponse>> getFilterValues(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromSingle(service.fetchFilters(tenantId), 200);
  }

  @PUT
  @Path("/ctas/{id}/live")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToLive(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(service.updateStatusToLive(tenantId, id), null, 200);
  }

  @PUT
  @Path("/ctas/{id}/pause")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToPause(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(service.updateStatusToPaused(tenantId, id), null, 200);
  }

  @PUT
  @Path("/ctas/{id}/schedule")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToScheduled(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(
        service.updateStatusToScheduled(tenantId, id), null, 200);
  }

  @PUT
  @Path("/ctas/{id}/conclude")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToConcluded(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(
        service.updateStatusToConcluded(tenantId, id), null, 200);
  }

  @PUT
  @Path("/ctas/{id}/terminate")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToTerminated(
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(
        service.updateStatusToTerminated(tenantId, id), null, 200);
  }
}
