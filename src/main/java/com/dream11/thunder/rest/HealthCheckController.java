package com.dream11.thunder.rest;

import com.dream11.thunder.client.AerospikeClient;
import com.dream11.thunder.client.AerospikeClientHolder;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Slf4j
@NoArgsConstructor
@Path("/healthcheck")
public class HealthCheckController {

    @GET
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<JsonObject> healthCheck() {
        AerospikeClient client = AerospikeClientHolder.get();
        String aerospikeStatus = (client != null && client.isConnected()) ? "UP" : "DOWN";

        JsonObject response = new JsonObject()
                .put("status", "UP")
                .put("service", "thunder")
                .put("aerospike", new JsonObject().put("status", aerospikeStatus));

        return java.util.concurrent.CompletableFuture.completedFuture(response);
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> ping() {
        return java.util.concurrent.CompletableFuture.completedFuture("pong");
    }
}

