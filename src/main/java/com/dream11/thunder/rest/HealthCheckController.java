package com.dream11.thunder.rest;

import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Slf4j
@Path("/healthcheck")
public class HealthCheckController {

    @GET
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<JsonObject> healthCheck() {
        JsonObject response = new JsonObject()
                .put("status", "UP")
                .put("service", "thunder");
        return java.util.concurrent.CompletableFuture.completedFuture(response);
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> ping() {
        return java.util.concurrent.CompletableFuture.completedFuture("pong");
    }
}

