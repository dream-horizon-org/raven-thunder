package com.dream11.thunder.rest;

import com.aerospike.client.Info;
import com.dream11.thunder.client.AerospikeClient;
import com.dream11.thunder.client.AerospikeClientHolder;
import com.dream11.thunder.config.AerospikeConfig;
import com.dream11.thunder.config.Config;
import com.dream11.thunder.util.SharedDataUtils;
import io.vertx.core.Vertx;
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

    private Vertx vertx;

    @jakarta.inject.Inject
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

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

