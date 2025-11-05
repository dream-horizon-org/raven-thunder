package com.dream11.thunder.verticle;

import com.dream11.thunder.client.AerospikeClient;
import com.dream11.thunder.client.AerospikeClientHolder;
import com.dream11.thunder.client.AerospikeClientImpl;
import com.dream11.thunder.config.AerospikeConfig;
import com.dream11.thunder.util.ConfigUtil;
import com.dream11.thunder.util.SharedDataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    private JsonObject config;
    private AerospikeClient aerospikeClient;

    @Override
    public Completable rxStart() {
        return ConfigUtil.getConfig(vertx)
                .map(config -> {
                    this.config = config;
                    SharedDataUtils.put(vertx.getDelegate(), config, "applicationConfig");
                    log.info("Configuration loaded successfully");
                    return config;
                })
                .flatMapCompletable(config -> initializeClients(config)
                        .andThen(deployRestVerticle(config)))
                .doOnComplete(() -> log.info("MainVerticle started successfully"));
    }

    @Override
    public Completable rxStop() {
        log.info("Stopping MainVerticle");
        if (aerospikeClient != null) {
            AerospikeClientHolder.clear();
            return aerospikeClient.rxClose();
        }
        return Completable.complete();
    }

    private Completable initializeClients(JsonObject config) {
        return initializeAerospikeClient(config);
    }

    private Completable initializeAerospikeClient(JsonObject cfg) {
        JsonObject aerospikeJson = cfg.getJsonObject("aerospike");
        if (aerospikeJson == null) {
            log.warn("Aerospike configuration not found, skipping Aerospike client initialization");
            return Completable.complete();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            AerospikeConfig aerospikeConfig = mapper.readValue(aerospikeJson.encode(), AerospikeConfig.class);
            this.aerospikeClient = new AerospikeClientImpl(aerospikeConfig);
            return aerospikeClient.rxConnect()
                    .doOnComplete(() -> {
                        AerospikeClientHolder.set(aerospikeClient);
                        log.info("Aerospike client initialized successfully");
                    });
        } catch (Exception e) {
            log.error("Failed to initialize Aerospike client", e);
            return Completable.error(e);
        }
    }

    private Completable deployRestVerticle(JsonObject cfg) {
        JsonObject server = cfg.getJsonObject("server", new JsonObject());
        String host = server.getString("host", "0.0.0.0");
        int port = server.getInteger("port", 8080);
        int instances = server.getInteger("instances", 1);
        boolean compressionSupported = server.getBoolean("compressionSupported", true);
        int idleTimeout = server.getInteger("idleTimeout", 60);

        log.info("Starting Thunder application on {}:{} with {} instance(s)", host, port, instances);

        HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setHost(host)
                .setPort(port)
                .setCompressionSupported(compressionSupported)
                .setIdleTimeout(idleTimeout);

        return vertx.rxDeployVerticle(
                () -> new RestVerticle(httpServerOptions),
                new DeploymentOptions().setInstances(instances)
        ).ignoreElement();
    }

    private Integer getNumOfCores() {
        return CpuCoreSensor.availableProcessors();
    }
}

