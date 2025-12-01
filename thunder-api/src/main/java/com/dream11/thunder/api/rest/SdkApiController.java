package com.dream11.thunder.api.rest;

import com.dream11.thunder.core.io.Response;
// Removed Dream11 RestResponse dependency;
import com.dream11.thunder.api.io.request.CTASnapshotRequest;
import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.api.service.SdkService;
import com.dream11.thunder.core.model.Nudge;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

@Slf4j
@Tag(
    name = "SDK",
    description = "APIs for SDK clients to interact with Thunder. " +
                  "These endpoints handle app launch, state machine synchronization, " +
                  "and nudge preview retrieval for mobile and web clients."
)
@Path("/cta")
public class SdkApiController {

  private final SdkService service;

  private final CTAResponse emptyCTAResponse = new CTAResponse();

  @Inject
  public SdkApiController(SdkService service) {
    this.service = service;
  }

  @Tag(name = "SDK")
  @Operation(
      summary = "Get Nudge by ID (Deprecated)",
      description = "Retrieves a Nudge template by its ID. " +
                    "⚠️ This endpoint is deprecated. Use GET /cta/nudge/preview/{id} instead.",
      operationId = "findNudge",
      deprecated = true
  )
  @APIResponse(
      responseCode = "200",
      description = "Nudge retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = Nudge.class)
      )
  )
  @APIResponse(
      responseCode = "404",
      description = "Nudge not found"
  )
  @GET
  @Deprecated
  @Path("/nudges/{id}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Nudge>> findNudge(
      @Parameter(
          name = "id",
          description = "Nudge ID",
          required = true,
          example = "5",
          schema = @Schema(type = SchemaType.STRING)
      )
      @PathParam("id") String id) {
    return ResponseWrapper.fromMaybe(service.findNudge(id), null, 200);
  }

  @Tag(name = "SDK")
  @Operation(
      summary = "Get Nudge Preview",
      description = "Retrieves a Nudge Preview by its ID. " +
                    "Nudge Previews contain the UI template and configuration that will be displayed to users. " +
                    "This is the recommended endpoint for retrieving nudge templates.",
      operationId = "getNudgePreview"
  )
  @APIResponse(
      responseCode = "200",
      description = "Nudge Preview retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = NudgePreview.class),
          examples = {
              @ExampleObject(
                  name = "Success Response",
                  summary = "Example nudge preview response",
                  value = "{\n" +
                          "  \"success\": true,\n" +
                          "  \"data\": {\n" +
                          "    \"id\": \"5\",\n" +
                          "    \"template\": \"{\\\"type\\\":\\\"BottomSheet\\\",\\\"testId\\\":\\\"nudge_container_bottom_sheet\\\"}\",\n" +
                          "    \"ttl\": 9999999\n" +
                          "  },\n" +
                          "  \"statusCode\": 200,\n" +
                          "  \"error\": null,\n" +
                          "  \"message\": null\n" +
                          "}"
              )
          }
      )
  )
  @APIResponse(
      responseCode = "404",
      description = "Nudge Preview not found"
  )
  @GET
  @Path("/nudge/preview/{id}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<NudgePreview>> getNudgePreview(
      @Parameter(
          name = "id",
          description = "Nudge Preview ID",
          required = true,
          example = "5",
          schema = @Schema(type = SchemaType.STRING)
      )
      @PathParam("id") String id,
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId) {
    return ResponseWrapper.fromMaybe(service.findNudgePreview(tenantId, id), null, 200);
  }

  @Tag(name = "SDK")
  @Operation(
      summary = "App Launch - Active State Machines",
      description = "Called by the client app on launch to synchronize state machines and retrieve active CTAs. " +
                    "The client sends its current state snapshot (CTAs and behaviour tags), and the server " +
                    "returns the updated state with active CTAs that should be shown to the user. " +
                    "Requires api_version header >= 1, otherwise returns empty response.",
      operationId = "appLaunch"
  )
  @APIResponse(
      responseCode = "200",
      description = "Active CTAs and state machines retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CTAResponse.class),
          examples = {
              @ExampleObject(
                  name = "Success Response",
                  summary = "Example app launch response",
                  value = "{\n" +
                          "  \"success\": true,\n" +
                          "  \"data\": {\n" +
                          "    \"ctas\": [\n" +
                          "      {\n" +
                          "        \"ctaId\": \"101\",\n" +
                          "        \"rule\": {\n" +
                          "          \"stateToAction\": {\"1\": \"actionId1\"},\n" +
                          "          \"resetStates\": [],\n" +
                          "          \"resetCTAonFirstLaunch\": false,\n" +
                          "          \"contextParams\": [\"mode\", \"contestId\"],\n" +
                          "          \"stateTransition\": {\"ContestJoinedClient\": {\"0\": [{\"transitionTo\": 1, \"filters\": {\"operator\": \"AND\", \"filter\": [{\"propertyName\": \"mode\", \"propertyType\": \"string\", \"comparisonType\": \"=\", \"comparisonValue\": \"normal\"}]}}]}},\n" +
                          "          \"groupByConfig\": {\"groupBy\": [\"roundId\"]},\n" +
                          "          \"priority\": 1,\n" +
                          "          \"stateMachineTTL\": 1812517298168,\n" +
                          "          \"ctaValidTill\": 1812517298168,\n" +
                          "          \"actions\": [{\"actionId1\": {\"type\": \"BottomSheet\", \"nudgeId\": \"5\", \"nudgeTemplate\": {\"testId\": \"nudge_container_bottom_sheet\"}}}],\n" +
                          "          \"frequency\": {\"session\": {\"limit\": 1}, \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 2}, \"lifespan\": {\"limit\": 10}}\n" +
                          "        },\n" +
                          "        \"activeStateMachines\": {\n" +
                          "          \"5\": {\n" +
                          "            \"currentState\": \"2\",\n" +
                          "            \"lastTransitionAt\": 1720166608502,\n" +
                          "            \"context\": {},\n" +
                          "            \"createdAt\": 1720166608502,\n" +
                          "            \"reset\": true\n" +
                          "          }\n" +
                          "        },\n" +
                          "        \"resetAt\": [1701603029000],\n" +
                          "        \"actionDoneAt\": [1756099199923],\n" +
                          "        \"behaviourTagName\": \"onboarding_eligible\"\n" +
                          "      }\n" +
                          "    ],\n" +
                          "    \"behaviourTags\": [\n" +
                          "      {\n" +
                          "        \"behaviourTagName\": \"onboarding_eligible\",\n" +
                          "        \"exposureRule\": {\n" +
                          "          \"session\": {\"limit\": 1},\n" +
                          "          \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 7}\n" +
                          "        },\n" +
                          "        \"ctaRelation\": {\n" +
                          "          \"activeCtas\": [1]\n" +
                          "        }\n" +
                          "      }\n" +
                          "    ]\n" +
                          "  },\n" +
                          "  \"statusCode\": 200,\n" +
                          "  \"error\": null,\n" +
                          "  \"message\": null\n" +
                          "}"
              )
          }
      )
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or missing required headers"
  )
  @POST
  @Path("/active/state-machines/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTAResponse>> appLaunch(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "auth-userid",
          description = "User ID of the authenticated user",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull(message = "auth-userid cannot be null") @HeaderParam("auth-userid") Long userId,
      @Parameter(
          name = "app_version",
          description = "Client app version",
          required = false,
          example = "1.2.3"
      )
      @Nullable @HeaderParam("app_version") String appVersion,
      @Parameter(
          name = "codepush_version",
          description = "CodePush version (for React Native apps)",
          required = false,
          example = "1.0.0"
      )
      @Nullable @HeaderParam("codepush_version") String cpVersion,
      @Parameter(
          name = "package_name",
          description = "App package name",
          required = false,
          example = "com.example.app"
      )
      @Nullable @HeaderParam("package_name") String packageName,
      @Parameter(
          name = "api_version",
          description = "API version. Must be >= 1 for the endpoint to process the request. " +
                       "If null or < 1, returns empty response.",
          required = false,
          example = "1",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @Nullable @HeaderParam("api_version") Long apiVersion,
      @RequestBody(
          description = "Current state snapshot from client including CTAs and behaviour tags",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CTASnapshotRequest.class),
              examples = {
                  @ExampleObject(
                      name = "App Launch Request",
                      summary = "Example request with CTAs and behaviour tags",
                      value = "{\n" +
                              "  \"ctas\": [\n" +
                              "    {\n" +
                              "      \"ctaId\": \"101\",\n" +
                              "      \"activeStateMachines\": {\n" +
                              "        \"5\": {\n" +
                              "          \"currentState\": \"2\",\n" +
                              "          \"lastTransitionAt\": 1720166608502,\n" +
                              "          \"context\": {},\n" +
                              "          \"createdAt\": 1720166608502,\n" +
                              "          \"reset\": true\n" +
                              "        }\n" +
                              "      },\n" +
                              "      \"resetAt\": [1701603029000],\n" +
                              "      \"actionDoneAt\": [1756099199923]\n" +
                              "    }\n" +
                              "  ],\n" +
                              "  \"behaviourTags\": [\n" +
                              "    {\n" +
                              "      \"behaviourTagName\": \"onboarding_eligible\",\n" +
                              "      \"exposureRule\": {\n" +
                              "        \"session\": {\"limit\": 1},\n" +
                              "        \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 7}\n" +
                              "      },\n" +
                              "      \"ctaRelation\": {\n" +
                              "        \"activeCtas\": [1]\n" +
                              "      }\n" +
                              "    }\n" +
                              "  ]\n" +
                              "}"
                  )
              }
          )
      )
      @Valid CTASnapshotRequest deltaSnapshot) {
    if (apiVersion == null || apiVersion < 1)
      return ResponseWrapper.fromMaybe(Maybe.just(emptyCTAResponse), emptyCTAResponse, 200);
    return ResponseWrapper.fromMaybe(
        service.appLaunch(tenantId, userId, deltaSnapshot), emptyCTAResponse, 200);
  }

  @Tag(name = "SDK")
  @Operation(
      summary = "App Launch v1 - Active State Machines",
      description = "Version 1 endpoint for app launch. This is an alias for POST /cta/active/state-machines/. " +
                    "Called by the client app on launch to synchronize state machines and retrieve active CTAs. " +
                    "The client sends its current state snapshot, and the server returns updated state with active CTAs.",
      operationId = "appLaunchV1"
  )
  @APIResponse(
      responseCode = "200",
      description = "Active CTAs and state machines retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CTAResponse.class)
      )
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or missing required headers"
  )
  @POST
  @Path("/v1/active/state-machines/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTAResponse>> appLaunchV1(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "auth-userid",
          description = "User ID of the authenticated user",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull(message = "auth-userid cannot be null") @HeaderParam("auth-userid") Long userId,
      @Parameter(
          name = "app_version",
          description = "Client app version",
          required = false,
          example = "1.2.3"
      )
      @Nullable @HeaderParam("app_version") String appVersion,
      @Parameter(
          name = "codepush_version",
          description = "CodePush version (for React Native apps)",
          required = false,
          example = "1.0.0"
      )
      @Nullable @HeaderParam("codepush_version") String cpVersion,
      @Parameter(
          name = "package_name",
          description = "App package name",
          required = false,
          example = "com.example.app"
      )
      @Nullable @HeaderParam("package_name") String packageName,
      @Parameter(
          name = "api_version",
          description = "API version. Must be >= 1 for the endpoint to process the request.",
          required = false,
          example = "1",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @Nullable @HeaderParam("api_version") Long apiVersion,
      @RequestBody(
          description = "Current state snapshot from client including CTAs and behaviour tags",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CTASnapshotRequest.class),
              examples = {
                  @ExampleObject(
                      name = "App Launch v1 Request",
                      summary = "Example request with empty CTAs",
                      value = "{\n" +
                              "  \"ctas\": []\n" +
                              "}"
                  )
              }
          )
      )
      @Valid CTASnapshotRequest deltaSnapshot) {
    // v1 endpoint is just an alias for the main endpoint
    return appLaunch(tenantId, userId, appVersion, cpVersion, packageName, apiVersion, deltaSnapshot);
  }

  @Tag(name = "SDK")
  @Operation(
      summary = "Merge Snapshot Delta",
      description = "Merges a delta snapshot from the client with the server state. " +
                    "This endpoint is used to synchronize incremental state changes (CTAs and behaviour tags) " +
                    "between the client and server. The server merges the delta and updates the user's state. " +
                    "Requires api_version header >= 1, otherwise returns false.",
      operationId = "mergeSnapshotDelta"
  )
  @APIResponse(
      responseCode = "200",
      description = "Snapshot delta merged successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(type = SchemaType.BOOLEAN),
          examples = {
              @ExampleObject(
                  name = "Success Response",
                  summary = "Merge successful",
                  value = "{\n" +
                          "  \"success\": true,\n" +
                          "  \"data\": true,\n" +
                          "  \"statusCode\": 200,\n" +
                          "  \"error\": null,\n" +
                          "  \"message\": null\n" +
                          "}"
              )
          }
      )
  )
  @APIResponse(
      responseCode = "400",
      description = "Invalid request data or missing required headers"
  )
  @POST
  @Path("/state-machines/snapshot/delta/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Boolean>> merge(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "auth-userid",
          description = "User ID of the authenticated user",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull(message = "auth-userid cannot be null") @HeaderParam("auth-userid") Long userId,
      @Parameter(
          name = "app_version",
          description = "Client app version",
          required = false,
          example = "1.2.3"
      )
      @Nullable @HeaderParam("app_version") String appVersion,
      @Parameter(
          name = "codepush_version",
          description = "CodePush version (for React Native apps)",
          required = false,
          example = "1.0.0"
      )
      @Nullable @HeaderParam("codepush_version") String cpVersion,
      @Parameter(
          name = "package_name",
          description = "App package name",
          required = false,
          example = "com.example.app"
      )
      @Nullable @HeaderParam("package_name") String packageName,
      @Parameter(
          name = "api_version",
          description = "API version. Must be >= 1 for the endpoint to process the request. " +
                       "If null or < 1, returns false.",
          required = false,
          example = "1",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @Nullable @HeaderParam("api_version") Long apiVersion,
      @RequestBody(
          description = "Delta snapshot containing updated CTAs and behaviour tags to merge with server state",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CTASnapshotRequest.class),
              examples = {
                  @ExampleObject(
                      name = "Merge Delta Request",
                      summary = "Example request to merge snapshot delta",
                      value = "{\n" +
                              "  \"ctas\": [\n" +
                              "    {\n" +
                              "      \"ctaId\": \"101\",\n" +
                              "      \"activeStateMachines\": {\n" +
                              "        \"default\": {\n" +
                              "          \"currentState\": \"1\",\n" +
                              "          \"lastTransitionAt\": 1699999999000,\n" +
                              "          \"context\": {\n" +
                              "            \"cohort\": \"new_user\",\n" +
                              "            \"appVersion\": \"1.2.3\"\n" +
                              "          },\n" +
                              "          \"createdAt\": 1699900000000,\n" +
                              "          \"reset\": false\n" +
                              "        }\n" +
                              "      },\n" +
                              "      \"resetAt\": [1699800000000],\n" +
                              "      \"actionDoneAt\": [1699850000000]\n" +
                              "    },\n" +
                              "    {\n" +
                              "      \"ctaId\": \"202\",\n" +
                              "      \"activeStateMachines\": {},\n" +
                              "      \"resetAt\": [],\n" +
                              "      \"actionDoneAt\": []\n" +
                              "    }\n" +
                              "  ],\n" +
                              "  \"behaviourTags\": [\n" +
                              "    {\n" +
                              "      \"behaviourTagName\": \"onboarding_eligible\",\n" +
                              "      \"exposureRule\": {\n" +
                              "        \"session\": {\"limit\": 2},\n" +
                              "        \"lifespan\": {\"limit\": 10},\n" +
                              "        \"window\": {\"limit\": 3, \"unit\": \"days\", \"value\": 7},\n" +
                              "        \"ctasResetAt\": [\n" +
                              "          {\n" +
                              "            \"ctaId\": \"101\",\n" +
                              "            \"resetAt\": 1699700000000\n" +
                              "          }\n" +
                              "        ]\n" +
                              "      },\n" +
                              "      \"ctaRelation\": {\n" +
                              "        \"shownCta\": {\"rule\": \"LIST\", \"ctaList\": [\"101\"]},\n" +
                              "        \"hideCta\": {\"rule\": \"ANY\", \"ctaList\": []},\n" +
                              "        \"activeCtas\": [\"101\"]\n" +
                              "      }\n" +
                              "    }\n" +
                              "  ]\n" +
                              "}"
                  )
              }
          )
      )
      @Valid CTASnapshotRequest deltaSnapshot) {
    if (apiVersion == null || apiVersion < 1)
      return ResponseWrapper.fromSingle(Single.just(false), 200);
    return ResponseWrapper.fromSingle(service.merge(tenantId, userId, deltaSnapshot), 200);
  }
}
