package com.dream11.thunder.api.rest;

import com.dream11.common.util.CompletableFutureUtils;
import com.dream11.rest.healthcheck.HealthCheckResponse;
import com.dream11.rest.healthcheck.HealthCheckUtil;
import com.dream11.thunder.core.dao.HealthCheckDao;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import io.reactivex.rxjava3.Single;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.vertx.core.json.JsonObject;
import java.util.concurrent.CompletionStage;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Path("/healthcheck")
public class HealthCheck {

  private final HealthCheckDao healthCheckDao;

  @GET
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponse(content = @Content(schema = @Schema(implementation = HealthCheckResponse.class)))
  public CompletionStage<HealthCheckResponse> healthcheck() {
    val map =
        ImmutableMap.<String, Single<JsonObject>>builder()
            .put("maintenance", healthCheckDao.maintenanceHealthCheck())
            .put("aerospike", healthCheckDao.aerospikeHealthCheck())
            .build();
    return HealthCheckUtil.handler(map).to(CompletableFutureUtils::fromSingle);
  }
}
