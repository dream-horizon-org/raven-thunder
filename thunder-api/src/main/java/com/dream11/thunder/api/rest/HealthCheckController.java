package com.dream11.thunder.api.rest;

import com.aerospike.client.Info;
import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.client.AerospikeClientHolder;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.config.Config;
import com.dream11.thunder.core.util.SharedDataUtils;
import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

/**
 * Health endpoints for thunder-api service.
 * - GET /healthcheck: returns service and Aerospike connectivity status
 * - GET /healthcheck/ping: simple liveness probe
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Path("/healthcheck")
public class HealthCheckController {

    private final Vertx vertx;

    @GET
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<JsonObject> healthCheck() {
        AerospikeClient client = AerospikeClientHolder.get();
        JsonObject aerospikeStatus = new JsonObject();
        
        if (client == null || !client.isConnected()) {
            aerospikeStatus.put("status", "DOWN");
            aerospikeStatus.put("namespaces", new JsonObject());
        } else {
            aerospikeStatus.put("status", "UP");
            
            // Get namespace statuses
            JsonObject namespaces = new JsonObject();
            
            try {
                Config config = SharedDataUtils.get(vertx, Config.class);
                if (config != null && config.getAerospike() != null) {
                    AerospikeConfig aerospikeConfig = config.getAerospike();
                    
                    // Check user-data-namespace
                    String userNamespace = aerospikeConfig.getUserDataNamespace();
                    if (userNamespace != null) {
                        namespaces.put(userNamespace, checkNamespaceStatus(client, userNamespace));
                    }
                    
                    // Check admin-data-namespace
                    String adminNamespace = aerospikeConfig.getAdminDataNamespace();
                    if (adminNamespace != null) {
                        namespaces.put(adminNamespace, checkNamespaceStatus(client, adminNamespace));
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to check namespace statuses", e);
                namespaces.put("error", "Unable to check namespace status");
            }
            
            aerospikeStatus.put("namespaces", namespaces);
        }

        JsonObject response = new JsonObject()
                .put("status", "UP")
                .put("service", "thunder")
                .put("aerospike", aerospikeStatus);

        return java.util.concurrent.CompletableFuture.completedFuture(response);
    }

    private String checkNamespaceStatus(AerospikeClient client, String namespace) {
        try {
            String info = Info.request(client.getClient().getNodes()[0], "namespace/" + namespace);
            if (info != null && !info.isEmpty()) {
                // Check if namespace exists and is accessible
                return "UP";
            }
        } catch (Exception e) {
            log.debug("Failed to check namespace {} status: {}", namespace, e.getMessage());
        }
        return "DOWN";
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> ping() {
        return java.util.concurrent.CompletableFuture.completedFuture("pong");
    }
}

