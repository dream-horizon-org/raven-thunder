package com.dream11.thunder.api.verticle;

import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.client.AerospikeClientHolder;
import com.dream11.thunder.core.client.AerospikeClientImpl;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.config.Config;
import com.dream11.thunder.core.config.ServerConfig;
import com.dream11.thunder.core.util.ConfigUtil;
import com.dream11.thunder.core.util.SharedDataUtils;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.rxjava3.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  private Config config;
  private AerospikeClient aerospikeClient;

  @Override
  public Completable rxStart() {
    return ConfigUtil.getConfig(vertx, "thunder-default.conf", "thunder.conf")
        .map(
            config -> {
              this.config = config;
              SharedDataUtils.put(vertx.getDelegate(), config);
              log.info("Configuration loaded successfully");
              return config;
            })
        .flatMapCompletable(config -> initializeClients(config).andThen(deployRestVerticle(config)))
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

  private Completable initializeClients(Config config) {
    return initializeAerospikeClient(config);
  }

  private Completable initializeAerospikeClient(Config cfg) {
    AerospikeConfig aerospikeConfig = cfg.getAerospike();
    if (aerospikeConfig == null) {
      log.warn("Aerospike configuration not found, skipping Aerospike client initialization");
      return Completable.complete();
    }
    try {
      this.aerospikeClient = new AerospikeClientImpl(aerospikeConfig);
      return aerospikeClient
          .rxConnect()
          .doOnComplete(
              () -> {
                AerospikeClientHolder.set(aerospikeClient);
                log.info("Aerospike client initialized successfully");
              })
          .doOnError(
              error -> {
                log.error("Failed to connect to Aerospike, service startup will fail", error);
              });
    } catch (Exception e) {
      log.error("Failed to initialize Aerospike client, service startup will fail", e);
      return Completable.error(e);
    }
  }

  private Completable deployRestVerticle(Config cfg) {
    ServerConfig server = cfg.getServer();
    if (server == null) {
      log.error("Server configuration not found!");
      return Completable.error(new IllegalStateException("Server configuration is required"));
    }

    String host = server.getHost() != null ? server.getHost() : "0.0.0.0";
    int port = server.getPort() != null ? server.getPort() : 8080;
    int instances = server.getInstances() != null ? server.getInstances() : 1;
    boolean compressionSupported =
        server.getCompressionSupported() != null ? server.getCompressionSupported() : true;
    int idleTimeout = server.getIdleTimeout() != null ? server.getIdleTimeout() : 60;

    log.info(
        "Starting Thunder API application on {}:{} with {} instance(s)", host, port, instances);

    HttpServerOptions httpServerOptions =
        new HttpServerOptions()
            .setHost(host)
            .setPort(port)
            .setCompressionSupported(compressionSupported)
            .setIdleTimeout(idleTimeout);

    return vertx
        .rxDeployVerticle(
            () -> new RestVerticle(httpServerOptions),
            new DeploymentOptions().setInstances(instances))
        .ignoreElement();
  }

  private Integer getNumOfCores() {
    return CpuCoreSensor.availableProcessors();
  }
}
