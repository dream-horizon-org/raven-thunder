package com.raven.thunder.admin.rest;

import static com.raven.thunder.admin.constant.Constants.USER_ID_NULL_ERROR_MESSAGE;

import com.raven.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.raven.thunder.admin.io.request.BehaviourTagPutRequest;
import com.raven.thunder.admin.io.response.BehaviourTagsResponse;
import com.raven.thunder.admin.service.BehaviourTagService;
import com.raven.thunder.core.io.Response;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.util.ResponseWrapper;
import com.google.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** REST controller for behaviour tag management (list, detail, create, update). */
@Slf4j
@Tag(
    name = "Behaviour Tags",
    description =
        "APIs for managing Behaviour Tags. "
            + "Behaviour Tags define user segments and their exposure rules for CTAs. "
            + "They control which CTAs are shown or hidden to specific user groups based on "
            + "session limits, time windows, and CTA relationships.")
@Path("/thunder")
public class BehaviourTagController {

  private final BehaviourTagService service;

  @Inject
  public BehaviourTagController(BehaviourTagService service) {
    this.service = service;
  }

  @Tag(name = "Behaviour Tags")
  @Operation(
      summary = "List all Behaviour Tags",
      description =
          "Retrieves a list of all Behaviour Tags for the specified tenant. "
              + "Behaviour Tags define user segments and their exposure rules for CTAs.",
      operationId = "listBehaviourTags")
  @APIResponse(
      responseCode = "200",
      description = "List of Behaviour Tags retrieved successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = BehaviourTagsResponse.class),
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Example list of behaviour tags",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": {\n"
                            + "    \"behaviourTags\": [\n"
                            + "      {\n"
                            + "        \"name\": \"onboarding_eligible\",\n"
                            + "        \"description\": \"Users eligible for onboarding nudges\",\n"
                            + "        \"exposureRule\": {\n"
                            + "          \"session\": {\"limit\": 2},\n"
                            + "          \"window\": {\"limit\": 3, \"unit\": \"days\", \"value\": 7},\n"
                            + "          \"lifespan\": {\"limit\": 10}\n"
                            + "        },\n"
                            + "        \"ctaRelation\": {\n"
                            + "          \"shownCta\": {\"rule\": \"LIST\", \"ctaList\": [\"cta-101\"]},\n"
                            + "          \"hideCta\": {\"rule\": \"ANY\", \"ctaList\": []}\n"
                            + "        },\n"
                            + "        \"linkedCtas\": [\"cta-101\"],\n"
                            + "        \"createdAt\": 1609459200000,\n"
                            + "        \"createdBy\": \"admin@example.com\",\n"
                            + "        \"lastUpdatedAt\": 1609459200000,\n"
                            + "        \"lastUpdatedBy\": \"admin@example.com\",\n"
                            + "        \"tenantId\": \"tenant1\"\n"
                            + "      }\n"
                            + "    ]\n"
                            + "  },\n"
                            + "  \"statusCode\": 200,\n"
                            + "  \"error\": null,\n"
                            + "  \"message\": null\n"
                            + "}")
              }))
  @GET
  @Path("/behaviour-tags/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<BehaviourTagsResponse>> getBehaviourTags(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant identifier",
              required = false,
              example = "tenant1")
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId) {
    return ResponseWrapper.fromSingle(service.fetchAllBehaviourTags(tenantId), 200);
  }

  @Tag(name = "Behaviour Tags")
  @Operation(
      summary = "Get Behaviour Tag by name",
      description =
          "Retrieves a specific Behaviour Tag by its name. "
              + "Returns the complete Behaviour Tag object including all metadata, "
              + "exposure rules, CTA relations, and linked CTAs.",
      operationId = "getBehaviourTag")
  @APIResponse(
      responseCode = "200",
      description = "Behaviour Tag retrieved successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = BehaviourTag.class),
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Example behaviour tag response",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": {\n"
                            + "    \"name\": \"onboarding_eligible\",\n"
                            + "    \"description\": \"Users eligible for onboarding nudges\",\n"
                            + "    \"exposureRule\": {\n"
                            + "      \"session\": {\"limit\": 2},\n"
                            + "      \"window\": {\"limit\": 3, \"unit\": \"days\", \"value\": 7},\n"
                            + "      \"lifespan\": {\"limit\": 10}\n"
                            + "    },\n"
                            + "    \"ctaRelation\": {\n"
                            + "      \"shownCta\": {\"rule\": \"LIST\", \"ctaList\": [\"cta-101\"]},\n"
                            + "      \"hideCta\": {\"rule\": \"ANY\", \"ctaList\": []}\n"
                            + "    },\n"
                            + "    \"linkedCtas\": [\"cta-101\"],\n"
                            + "    \"createdAt\": 1609459200000,\n"
                            + "    \"createdBy\": \"admin@example.com\",\n"
                            + "    \"lastUpdatedAt\": 1609459200000,\n"
                            + "    \"lastUpdatedBy\": \"admin@example.com\",\n"
                            + "    \"tenantId\": \"tenant1\"\n"
                            + "  },\n"
                            + "  \"statusCode\": 200,\n"
                            + "  \"error\": null,\n"
                            + "  \"message\": null\n"
                            + "}")
              }))
  @APIResponse(responseCode = "404", description = "Behaviour Tag not found")
  @GET
  @Path("/behaviour-tags/{behaviourTagName}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<BehaviourTag>> getBehaviourTags(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant identifier",
              required = false,
              example = "tenant1")
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @Parameter(
              name = "behaviourTagName",
              description = "Name of the Behaviour Tag to retrieve",
              required = true,
              example = "onboarding_eligible",
              schema = @Schema(type = SchemaType.STRING))
          @PathParam("behaviourTagName")
          String behaviourTagName) {
    return ResponseWrapper.fromSingle(
        service.fetchBehaviourTagDetail(tenantId, behaviourTagName), 200);
  }

  @Tag(name = "Behaviour Tags")
  @Operation(
      summary = "Create a new Behaviour Tag",
      description =
          "Creates a new Behaviour Tag with exposure rules and CTA relations. "
              + "Behaviour Tags define user segments and control which CTAs are shown or hidden. "
              + "The exposureRule defines frequency limits (session, window, lifespan). "
              + "The ctaRelation defines which CTAs to show (shownCta) and hide (hideCta).",
      operationId = "createBehaviourTag")
  @APIResponse(
      responseCode = "200",
      description = "Behaviour Tag created successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Behaviour tag created",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": null,\n"
                            + "  \"statusCode\": 200,\n"
                            + "  \"error\": null,\n"
                            + "  \"message\": null\n"
                            + "}")
              }))
  @APIResponse(responseCode = "400", description = "Invalid request data or validation failed")
  @POST
  @Path("/behaviour-tags/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> createBehaviour(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant identifier",
              required = false,
              example = "tenant1")
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @RequestBody(
              description =
                  "Behaviour Tag creation request with name, description, exposure rules, and CTA relations",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = BehaviourTagCreateRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Create Request",
                            summary = "Example request to create a behaviour tag",
                            value =
                                "{\n"
                                    + "  \"behaviourTagName\": \"onboarding_eligible\",\n"
                                    + "  \"description\": \"Users eligible for onboarding nudges\",\n"
                                    + "  \"linkedCtas\": [],\n"
                                    + "  \"exposureRule\": {\n"
                                    + "    \"session\": {\"limit\": 2},\n"
                                    + "    \"window\": {\"limit\": 3, \"unit\": \"days\", \"value\": 7},\n"
                                    + "    \"lifespan\": {\"limit\": 10}\n"
                                    + "  },\n"
                                    + "  \"ctaRelation\": {\n"
                                    + "    \"shownCta\": {\"rule\": \"LIST\", \"ctaList\": [\"cta-101\"]},\n"
                                    + "    \"hideCta\": {\"rule\": \"ANY\", \"ctaList\": []}\n"
                                    + "  }\n"
                                    + "}")
                      }))
          @NotNull
          @Valid
          BehaviourTagCreateRequest behaviourTagCreateRequest,
      @Parameter(
              name = "user",
              description = "User ID of the admin creating the Behaviour Tag",
              required = true,
              example = "admin@example.com")
          @NotNull(message = USER_ID_NULL_ERROR_MESSAGE)
          @HeaderParam("user")
          String userId) {
    behaviourTagCreateRequest.validate();
    return ResponseWrapper.fromCompletable(
        service.createBehaviourTag(tenantId, userId, behaviourTagCreateRequest), null, 200);
  }

  @Tag(name = "Behaviour Tags")
  @Operation(
      summary = "Update an existing Behaviour Tag",
      description =
          "Updates an existing Behaviour Tag's description, exposure rules, and CTA relations. "
              + "Only the fields provided in the request will be updated. "
              + "The behaviourTagName in the path must match the tag being updated.",
      operationId = "updateBehaviourTag")
  @APIResponse(
      responseCode = "200",
      description = "Behaviour Tag updated successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              examples = {
                @ExampleObject(
                    name = "Success Response",
                    summary = "Behaviour tag updated",
                    value =
                        "{\n"
                            + "  \"success\": true,\n"
                            + "  \"data\": null,\n"
                            + "  \"statusCode\": 200,\n"
                            + "  \"error\": null,\n"
                            + "  \"message\": null\n"
                            + "}")
              }))
  @APIResponse(responseCode = "400", description = "Invalid request data or validation failed")
  @APIResponse(responseCode = "404", description = "Behaviour Tag not found")
  @PUT
  @Path("/behaviour-tags/{behaviourTagName}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response<Object>> updateBehaviourTag(
      @Parameter(
              name = "x-tenant-id",
              description = "Tenant identifier",
              required = false,
              example = "tenant1")
          @DefaultValue("default")
          @HeaderParam("x-tenant-id")
          String tenantId,
      @RequestBody(
              description =
                  "Behaviour Tag update request with description, exposure rules, and CTA relations",
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON,
                      schema = @Schema(implementation = BehaviourTagPutRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Update Request",
                            summary = "Example request to update a behaviour tag",
                            value =
                                "{\n"
                                    + "  \"description\": \"Updated description for onboarding eligible users\",\n"
                                    + "  \"exposureRule\": {\n"
                                    + "    \"session\": {\"limit\": 1},\n"
                                    + "    \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 7},\n"
                                    + "    \"lifespan\": {\"limit\": 5}\n"
                                    + "  },\n"
                                    + "  \"ctaRelation\": {\n"
                                    + "    \"shownCta\": {\"rule\": \"ANY\"},\n"
                                    + "    \"hideCta\": {\"rule\": \"NONE\"}\n"
                                    + "  },\n"
                                    + "  \"linkedCtas\": []\n"
                                    + "}")
                      }))
          @NotNull
          @Valid
          BehaviourTagPutRequest behaviourTagPutRequest,
      @Parameter(
              name = "behaviourTagName",
              description = "Name of the Behaviour Tag to update",
              required = true,
              example = "onboarding_eligible",
              schema = @Schema(type = SchemaType.STRING))
          @NotNull
          @PathParam("behaviourTagName")
          String behaviourTagName,
      @Parameter(
              name = "user",
              description = "User ID of the admin updating the Behaviour Tag",
              required = true,
              example = "admin@example.com")
          @NotNull(message = USER_ID_NULL_ERROR_MESSAGE)
          @HeaderParam("user")
          String userId) {
    behaviourTagPutRequest.validate(behaviourTagName);
    return ResponseWrapper.fromCompletable(
        service.updateBehaviourTag(tenantId, behaviourTagName, behaviourTagPutRequest, userId),
        null,
        200);
  }
}
