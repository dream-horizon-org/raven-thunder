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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

@Slf4j
@Tag(
    name = "Debug",
    description = "Debug APIs for inspecting CTA rules, active CTAs, and user state machines. " +
                  "These endpoints are useful for debugging and troubleshooting CTA behavior " +
                  "in development and staging environments."
)
@Path("/cta")
public class AppDebugController {

  private final AppDebugService service;
  Map<String, Nudge> emptyResponse = new HashMap<>();

  @Inject
  public AppDebugController(AppDebugService service) {
    this.service = service;
  }

  @Tag(name = "Debug")
  @Operation(
      summary = "Get All Active CTA Rules",
      description = "Retrieves all active CTA rules for the specified tenant. " +
                    "Active CTAs are those that are currently enabled and available for display. " +
                    "Use the cache parameter to control whether to use cached data or fetch fresh data.",
      operationId = "findAllActiveCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "Active CTA rules retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(
              type = SchemaType.OBJECT,
              description = "Map of CTA ID (Long) to CTA object"
          ),
          examples = {
              @ExampleObject(
                  name = "Success Response",
                  summary = "Example active CTAs response",
                  value = "{\n" +
                          "  \"success\": true,\n" +
                          "  \"data\": {\n" +
                          "    \"101\": {\n" +
                          "      \"id\": 101,\n" +
                          "      \"name\": \"Welcome Nudge\",\n" +
                          "      \"status\": \"ACTIVE\",\n" +
                          "      \"rule\": {\n" +
                          "        \"stateToAction\": {\"1\": \"actionId1\"},\n" +
                          "        \"resetStates\": [],\n" +
                          "        \"resetCTAonFirstLaunch\": false,\n" +
                          "        \"contextParams\": [\"mode\", \"contestId\"],\n" +
                          "        \"stateTransition\": {},\n" +
                          "        \"groupByConfig\": {\"groupBy\": [\"roundId\"]},\n" +
                          "        \"priority\": 1,\n" +
                          "        \"actions\": [{\"actionId1\": {\"type\": \"BottomSheet\", \"nudgeId\": \"5\"}}],\n" +
                          "        \"frequency\": {\"session\": {\"limit\": 1}, \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 2}, \"lifespan\": {\"limit\": 10}}\n" +
                          "      },\n" +
                          "      \"cohortEligibility\": {\n" +
                          "        \"includedCohorts\": [\"all\"],\n" +
                          "        \"excludedCohorts\": []\n" +
                          "      },\n" +
                          "      \"behaviourTags\": [\"onboarding_eligible\"],\n" +
                          "      \"tenantId\": \"tenant1\"\n" +
                          "    },\n" +
                          "    \"202\": {\n" +
                          "      \"id\": 202,\n" +
                          "      \"name\": \"Feature Announcement\",\n" +
                          "      \"status\": \"ACTIVE\",\n" +
                          "      \"rule\": {},\n" +
                          "      \"cohortEligibility\": {\n" +
                          "        \"includedCohorts\": [\"all\"],\n" +
                          "        \"excludedCohorts\": []\n" +
                          "      },\n" +
                          "      \"behaviourTags\": [],\n" +
                          "      \"tenantId\": \"tenant1\"\n" +
                          "    }\n" +
                          "  },\n" +
                          "  \"statusCode\": 200,\n" +
                          "  \"error\": null,\n" +
                          "  \"message\": null\n" +
                          "}"
              )
          }
      )
  )
  @GET
  @Path("/rules/active")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Map<Long, CTA>>> findAllActiveCTA(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "cache",
          description = "Whether to use cached data (true) or fetch fresh data (false)",
          required = true,
          example = "true",
          schema = @Schema(type = SchemaType.BOOLEAN)
      )
      @NotNull @QueryParam("cache") Boolean cache) {
    return ResponseWrapper.fromSingle(service.findAllActiveCTA(tenantId, cache), 200);
  }

  @Tag(name = "Debug")
  @Operation(
      summary = "Get CTA Rule",
      description = "Retrieves a specific CTA rule by its ID. " +
                    "Returns the complete CTA object including rule configuration, " +
                    "cohort eligibility, behaviour tags, and status.",
      operationId = "findCTA"
  )
  @APIResponse(
      responseCode = "200",
      description = "CTA rule retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CTA.class),
          examples = {
              @ExampleObject(
                  name = "Success Response",
                  summary = "Example CTA rule response",
                  value = "{\n" +
                          "  \"success\": true,\n" +
                          "  \"data\": {\n" +
                          "    \"id\": 101,\n" +
                          "    \"name\": \"Welcome Nudge\",\n" +
                          "    \"status\": \"ACTIVE\",\n" +
                          "    \"rule\": {\n" +
                          "      \"stateToAction\": {\"1\": \"actionId1\"},\n" +
                          "      \"resetStates\": [],\n" +
                          "      \"resetCTAonFirstLaunch\": false,\n" +
                          "      \"contextParams\": [\"mode\", \"contestId\"],\n" +
                          "      \"stateTransition\": {\n" +
                          "        \"ContestJoinedClient\": {\n" +
                          "          \"0\": [\n" +
                          "            {\n" +
                          "              \"transitionTo\": 1,\n" +
                          "              \"filters\": {\n" +
                          "                \"operator\": \"AND\",\n" +
                          "                \"filter\": [\n" +
                          "                  {\n" +
                          "                    \"propertyName\": \"mode\",\n" +
                          "                    \"propertyType\": \"string\",\n" +
                          "                    \"comparisonType\": \"=\",\n" +
                          "                    \"comparisonValue\": \"normal\"\n" +
                          "                  }\n" +
                          "                ]\n" +
                          "              }\n" +
                          "            }\n" +
                          "          ]\n" +
                          "        }\n" +
                          "      },\n" +
                          "      \"groupByConfig\": {\"groupBy\": [\"roundId\"]},\n" +
                          "      \"priority\": 1,\n" +
                          "      \"stateMachineTTL\": 1812517298168,\n" +
                          "      \"ctaValidTill\": 1812517298168,\n" +
                          "      \"actions\": [\n" +
                          "        {\n" +
                          "          \"actionId1\": {\n" +
                          "            \"type\": \"BottomSheet\",\n" +
                          "            \"nudgeId\": \"5\",\n" +
                          "            \"nudgeTemplate\": {\"testId\": \"nudge_container_bottom_sheet\"}\n" +
                          "          }\n" +
                          "        }\n" +
                          "      ],\n" +
                          "      \"frequency\": {\n" +
                          "        \"session\": {\"limit\": 1},\n" +
                          "        \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 2},\n" +
                          "        \"lifespan\": {\"limit\": 10}\n" +
                          "      }\n" +
                          "    },\n" +
                          "    \"cohortEligibility\": {\n" +
                          "      \"includedCohorts\": [\"all\"],\n" +
                          "      \"excludedCohorts\": []\n" +
                          "    },\n" +
                          "    \"behaviourTags\": [\"onboarding_eligible\"],\n" +
                          "    \"tenantId\": \"tenant1\",\n" +
                          "    \"createdAt\": 1609459200000,\n" +
                          "    \"createdBy\": \"admin@example.com\",\n" +
                          "    \"lastUpdatedAt\": 1609459200000,\n" +
                          "    \"lastUpdatedBy\": \"admin@example.com\"\n" +
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
      description = "CTA rule not found"
  )
  @GET
  @Path("/rules/{id}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<CTA>> findCTA(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "id",
          description = "CTA ID",
          required = true,
          example = "101",
          schema = @Schema(type = SchemaType.STRING)
      )
      @NotNull @PathParam("id") String id) {
    return ResponseWrapper.fromMaybe(service.findCTA(tenantId, Long.parseLong(id)), null, 200);
  }

  @Tag(name = "Debug")
  @Operation(
      summary = "Get State Machine by User",
      description = "Retrieves the complete state machine snapshot for a specific user. " +
                    "This includes all active state machines for CTAs and behaviour tags. " +
                    "Useful for debugging user-specific state and understanding why certain CTAs " +
                    "are or aren't being shown to a user.",
      operationId = "findStateMachine"
  )
  @APIResponse(
      responseCode = "200",
      description = "User state machine snapshot retrieved successfully",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = UserDataSnapshot.class),
          examples = {
              @ExampleObject(
                  name = "Success Response",
                  summary = "Example user state machine snapshot",
                  value = "{\n" +
                          "  \"success\": true,\n" +
                          "  \"data\": {\n" +
                          "    \"stateMachines\": {\n" +
                          "      \"101\": {\n" +
                          "        \"ctaId\": \"101\",\n" +
                          "        \"activeStateMachines\": {\n" +
                          "          \"5\": {\n" +
                          "            \"currentState\": \"2\",\n" +
                          "            \"lastTransitionAt\": 1720166608502,\n" +
                          "            \"context\": {\"cohort\": \"new_user\", \"appVersion\": \"1.2.3\"},\n" +
                          "            \"createdAt\": 1720166608502,\n" +
                          "            \"reset\": true\n" +
                          "          }\n" +
                          "        },\n" +
                          "        \"resetAt\": [1701603029000],\n" +
                          "        \"actionDoneAt\": [1756099199923]\n" +
                          "      },\n" +
                          "      \"202\": {\n" +
                          "        \"ctaId\": \"202\",\n" +
                          "        \"activeStateMachines\": {},\n" +
                          "        \"resetAt\": [],\n" +
                          "        \"actionDoneAt\": []\n" +
                          "      }\n" +
                          "    },\n" +
                          "    \"behaviourTags\": {\n" +
                          "      \"onboarding_eligible\": {\n" +
                          "        \"behaviourTagName\": \"onboarding_eligible\",\n" +
                          "        \"exposureRule\": {\n" +
                          "          \"session\": {\"limit\": 2},\n" +
                          "          \"window\": {\"limit\": 3, \"unit\": \"days\", \"value\": 7},\n" +
                          "          \"lifespan\": {\"limit\": 10}\n" +
                          "        },\n" +
                          "        \"ctaRelation\": {\n" +
                          "          \"shownCta\": {\"rule\": \"LIST\", \"ctaList\": [\"101\"]},\n" +
                          "          \"hideCta\": {\"rule\": \"ANY\", \"ctaList\": []},\n" +
                          "          \"activeCtas\": [\"101\"]\n" +
                          "        }\n" +
                          "      }\n" +
                          "    }\n" +
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
      description = "User state machine not found"
  )
  @GET
  @Path("/state-machines")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<UserDataSnapshot>> find(
      @Parameter(
          name = "x-tenant-id",
          description = "Tenant identifier",
          required = false,
          example = "tenant1"
      )
      @DefaultValue("default") @HeaderParam("x-tenant-id") String tenantId,
      @Parameter(
          name = "auth-userid",
          description = "User ID to retrieve state machine for",
          required = true,
          example = "12345",
          schema = @Schema(type = SchemaType.INTEGER, format = "int64")
      )
      @NotNull @HeaderParam("auth-userid") Long userId) {
    return ResponseWrapper.fromMaybe(service.findStateMachine(tenantId, userId), null, 200);
  }
}
