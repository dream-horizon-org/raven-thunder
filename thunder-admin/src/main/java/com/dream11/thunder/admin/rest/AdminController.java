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
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.model.NudgePreview;
import com.dream11.thunder.core.util.FormatUtil;
import com.dream11.thunder.core.util.ResponseWrapper;
import com.google.inject.Inject;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

@Slf4j
@Tag(
    name = "Admin",
    description = "Admin APIs for managing CTAs (Call-to-Actions), Nudges, and Nudge Previews. " +
                  "Includes CRUD operations, status transitions, and filtering capabilities."
)
@Path("/thunder")
public class AdminController {

  private final AdminService service;
  private final NudgePreviewRepository nudgePreviewRepository;

  @Inject
  public AdminController(AdminService service, NudgePreviewRepository nudgePreviewRepository) {
    this.service = service;
    this.nudgePreviewRepository = nudgePreviewRepository;
  }

  @Operation(
      summary = "Create a new Nudge",
      description = "Creates a new Nudge template that can be used in CTAs. " +
                    "Nudges define the UI components and actions that will be displayed to users.",
      operationId = "createNudge"
  )
  @APIResponse(
      responseCode = "200",
      description = "Nudge created successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or validation failed"
  )
  @POST
  @Path("/nudges/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> createNudge(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier for multi-tenancy support",
          required = false,
          example = "default",
          schema = @Schema(type = SchemaType.STRING)
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @RequestBody(
          description = "Nudge template definition",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = Nudge.class)
          )
      )
      @NotNull @Valid Nudge template) {
    return ResponseWrapper.fromCompletable(service.createNudge(tenantId, template), null, 200);
  }

  @Operation(
      summary = "Update an existing Nudge",
      description = "Updates an existing Nudge template. All fields in the request will update the corresponding fields in the Nudge.",
      operationId = "updateNudge"
  )
  @APIResponse(
      responseCode = "200",
      description = "Nudge updated successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or validation failed"
  )
  @APIResponse(
      responseCode = "404",
      description = "Nudge not found"
  )
  @PUT
  @Path("/nudges/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateNudge(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "default"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @RequestBody(
          description = "Updated Nudge template definition",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = Nudge.class)
          )
      )
      @NotNull @Valid Nudge template) {
    return ResponseWrapper.fromCompletable(service.updateNudge(tenantId, template), null, 200);
  }

  @Operation(
      summary = "Create or Update Nudge Preview",
      description = "Creates a new Nudge Preview or updates an existing one if it already exists. " +
                    "Nudge Previews are used to preview nudge templates before they are used in CTAs. " +
                    "The preview includes the nudge template and TTL (time-to-live) configuration.",
      operationId = "createOrUpdateNudgePreview"
  )
  @APIResponse(
      responseCode = "200",
      description = "Nudge Preview created or updated successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or validation failed"
  )
  @POST
  @Path("/nudge/preview/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> createOrUpdateNudgePreview(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "default"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @RequestBody(
          description = "Nudge Preview definition with id, template, and TTL",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = NudgePreview.class)
          )
      )
      @NotNull @Valid NudgePreview nudgePreview) {
    nudgePreview.validate();
    return ResponseWrapper.fromCompletable(
        service.createOrUpdateNudgePreview(tenantId, nudgePreview), null, 200);
  }

  @Operation(
      summary = "Get Nudge Preview",
      description = "Retrieves a Nudge Preview by its ID. Returns the preview template and TTL configuration.",
      operationId = "getNudgePreview"
  )
  @APIResponse(
      responseCode = "200",
      description = "Nudge Preview retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = NudgePreview.class)
      )
  )
  @APIResponse(
      responseCode = "404",
      description = "Nudge Preview not found"
  )
  @GET
  @Path("/nudge/preview/")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<NudgePreview>> getNudgePreview(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "default"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "Unique identifier of the Nudge Preview",
          required = true,
          example = "preview_001",
          schema = @Schema(type = SchemaType.STRING)
      )
      @QueryParam("id") String id) {
    return ResponseWrapper.fromSingle(nudgePreviewRepository.find(tenantId, id).toSingle(), 200);
  }

  @Operation(
      summary = "Create CTA",
      description = "Creates a new Call-to-Action (CTA) with the provided details. " +
                    "The CTA will be created in DRAFT status and can be activated later using status update endpoints. " +
                    "Includes rule configuration, state machine setup, actions, and frequency controls.",
      operationId = "createCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA created successfully. Returns the new CTA ID.",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(type = SchemaType.INTEGER, format = "int64", description = "New CTA ID")
      )
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or validation failed"
  )
  @POST
  @Path("/ctas/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Long>> createCta(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier for multi-tenancy support",
          required = false,
          example = "tenant1",
          schema = @Schema(type = SchemaType.STRING)
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @RequestBody(
          description = "CTA creation request containing name, description, tags, team, rule configuration, " +
                       "state machine setup, actions, and frequency controls",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CTARequest.class)
          )
      )
      @NotNull @Valid CTARequest cta,
      @Parameter(
          name = "user",
          description = "User ID of the person creating the CTA",
          required = true,
          example = "admin@example.com",
          schema = @Schema(type = SchemaType.STRING)
      )
      @NotNull(message = USER_ID_NULL_ERROR_MESSAGE) @HeaderParam("user") String user) {
    cta.validate();
    return ResponseWrapper.fromSingle(service.createCTA(tenantId, cta, user), 200);
  }

  @Operation(
      summary = "Update CTA",
      description = "Updates an existing CTA. Only the fields provided in the request will be updated. " +
                    "Can update rule configuration, state machine setup, actions, and frequency controls.",
      operationId = "updateCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA updated successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or validation failed"
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @PUT
  @Path("/ctas/{ctaId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateCta(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @RequestBody(
          description = "CTA update request containing fields to update",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CTAUpdateRequest.class)
          )
      )
      @NotNull @Valid CTAUpdateRequest cta,
      @Parameter(
          name = "ctaId",
          description = "Unique identifier of the CTA to update",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("ctaId") Long ctaId,
      @Parameter(
          name = "user",
          description = "User ID of the person updating the CTA",
          required = true,
          example = "admin@example.com"
      )
      @NotNull(message = USER_ID_NULL_ERROR_MESSAGE) @HeaderParam("user") String user) {
    cta.validate();
    return ResponseWrapper.fromCompletable(
        service.updateCTA(tenantId, cta, ctaId, user), null, 200);
  }

  @Operation(
      summary = "Get CTA by ID",
      description = "Retrieves a specific CTA by its unique identifier. " +
                    "Returns the complete CTA object including all metadata, rules, status, and configuration.",
      operationId = "getCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CTA.class)
      )
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @GET
  @Path("/ctas/{ctaId}")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTA>> getCTA(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "ctaId",
          description = "Unique identifier of the CTA",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("ctaId") Long ctaId) {
    return ResponseWrapper.fromSingle(service.fetchCTA(tenantId, ctaId), 200);
  }

  @Operation(
      summary = "List CTAs",
      description = "Retrieves a paginated list of CTAs matching the specified filters. " +
                    "All query parameters are optional. Results are sorted by creation date (newest first). " +
                    "Use pagination parameters to control page size and navigation.",
      operationId = "listCTAs"
  )
  @APIResponse(
      responseCode = "200",
      description = "List of CTAs retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CTAListResponse.class)
      )
  )
  @GET
  @Path("/ctas/")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTAListResponse>> getCTAs(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "name",
          description = "Filter by exact CTA name (case-sensitive)",
          required = false,
          example = "Welcome Bonus"
      )
      @QueryParam("name") String name,
      @Parameter(
          name = "teams",
          description = "Comma-separated list of teams to filter by",
          required = false,
          example = "marketing,growth"
      )
      @QueryParam("teams") String teams,
      @Parameter(
          name = "tags",
          description = "Comma-separated list of tags to filter by",
          required = false,
          example = "promotion,signup"
      )
      @QueryParam("tags") String tags,
      @Parameter(
          name = "status",
          description = "Filter by CTA status",
          required = false,
          example = "LIVE",
          schema = @Schema(
              type = SchemaType.STRING,
              enumeration = {"DRAFT", "SCHEDULED", "LIVE", "PAUSED", "CONCLUDED", "TERMINATED"}
          )
      )
      @QueryParam("status") String status,
      @Parameter(
          name = "behaviourTag",
          description = "Filter by behaviour tag name",
          required = false,
          example = "new_user"
      )
      @QueryParam("behaviourTag") String behaviourTag,
      @Parameter(
          name = "createdBy",
          description = "Filter by creator user ID",
          required = false,
          example = "admin@example.com"
      )
      @QueryParam("createdBy") String createdBy,
      @Parameter(
          name = "searchName",
          description = "Search CTA names (partial match)",
          required = false,
          example = "welcome"
      )
      @QueryParam("searchName") String searchName,
      @Parameter(
          name = "pageNumber",
          description = "Page number (0-indexed). Default is 0.",
          required = false,
          example = "0",
          schema = @Schema(type = SchemaType.INTEGER, minimum = "0", defaultValue = "0")
      )
      @QueryParam("pageNumber") Integer pageNumber,
      @Parameter(
          name = "pageSize",
          description = "Number of items per page. Default is 10, maximum is 100.",
          required = false,
          example = "10",
          schema = @Schema(
              type = SchemaType.INTEGER,
              minimum = "1",
              maximum = "100",
              defaultValue = "10"
          )
      )
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

  @Operation(
      summary = "Get Filters",
      description = "Retrieves available filter values for CTAs including tags, teams, statuses, " +
                    "behaviour tags, and creators. Used to populate filter dropdowns in the admin UI.",
      operationId = "getFilters"
  )
  @APIResponse(
      responseCode = "200",
      description = "Filter values retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = FilterResponse.class)
      )
  )
  @GET
  @Path("/filters/")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<FilterResponse>> getFilterValues(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromSingle(service.fetchFilters(tenantId), 200);
  }

  @Operation(
      summary = "Activate CTA",
      description = "Changes the CTA status to LIVE. The CTA must be in SCHEDULED or PAUSED status. " +
                    "Once LIVE, the CTA will be available to end users through the SDK APIs.",
      operationId = "activateCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA activated successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "CTA cannot be activated from current status. Only SCHEDULED or PAUSED CTAs can be activated."
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @PUT
  @Path("/ctas/{id}/live")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToLive(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "Unique identifier of the CTA to activate",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(service.updateStatusToLive(tenantId, id), null, 200);
  }

  @Operation(
      summary = "Pause CTA",
      description = "Changes the CTA status to PAUSED. The CTA must be in LIVE status. " +
                    "Paused CTAs are not available to end users but can be reactivated later.",
      operationId = "pauseCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA paused successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "CTA cannot be paused from current status. Only LIVE CTAs can be paused."
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @PUT
  @Path("/ctas/{id}/pause")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToPause(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "Unique identifier of the CTA to pause",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(service.updateStatusToPaused(tenantId, id), null, 200);
  }

  @Operation(
      summary = "Schedule CTA",
      description = "Changes the CTA status to SCHEDULED. The CTA must be in DRAFT status. " +
                    "Scheduled CTAs will become LIVE automatically based on their startTime configuration.",
      operationId = "scheduleCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA scheduled successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "CTA cannot be scheduled from current status. Only DRAFT CTAs can be scheduled."
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @PUT
  @Path("/ctas/{id}/schedule")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToScheduled(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "Unique identifier of the CTA to schedule",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(
        service.updateStatusToScheduled(tenantId, id), null, 200);
  }

  @Operation(
      summary = "Conclude CTA",
      description = "Changes the CTA status to CONCLUDED. The CTA must be in LIVE or PAUSED status. " +
                    "Concluded CTAs have completed their lifecycle and are no longer available to end users.",
      operationId = "concludeCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA concluded successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "CTA cannot be concluded from current status. Only LIVE or PAUSED CTAs can be concluded."
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @PUT
  @Path("/ctas/{id}/conclude")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToConcluded(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "Unique identifier of the CTA to conclude",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(
        service.updateStatusToConcluded(tenantId, id), null, 200);
  }

  @Operation(
      summary = "Terminate CTA",
      description = "Changes the CTA status to TERMINATED. The CTA must be in DRAFT, SCHEDULED, LIVE, or PAUSED status. " +
                    "Terminated CTAs are permanently stopped and cannot be reactivated.",
      operationId = "terminateCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA terminated successfully"
  )
  @APIResponse(
      responseCode = "400",
      description = "CTA cannot be terminated from current status. Only DRAFT, SCHEDULED, LIVE, or PAUSED CTAs can be terminated."
  )
  @APIResponse(
      responseCode = "404",
      description = "CTA not found"
  )
  @PUT
  @Path("/ctas/{id}/terminate")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateStatusToTerminated(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "Unique identifier of the CTA to terminate",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @PathParam("id") Long id) {
    return ResponseWrapper.fromCompletable(
        service.updateStatusToTerminated(tenantId, id), null, 200);
  }
}
