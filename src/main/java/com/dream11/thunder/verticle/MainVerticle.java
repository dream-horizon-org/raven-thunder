package com.dream11.thunder.verticle;

import com.dream11.thunder.util.ConfigUtil;
import com.dream11.thunder.util.SharedDataUtils;
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

    @Override
    public Completable rxStart() {
        return ConfigUtil.getConfig(vertx)
                .map(config -> {
                    this.config = config;
                    SharedDataUtils.put(vertx.getDelegate(), config, "applicationConfig");
                    log.info("Configuration loaded successfully");
                    return config;
                })
                .flatMapCompletable(config -> deployRestVerticle(config))
                .doOnComplete(() -> log.info("MainVerticle started successfully"));
    }

    @Override
    public Completable rxStop() {
        log.info("Stopping MainVerticle");
        return Completable.complete();
    }

    private Completable deployRestVerticle(JsonObject config) {
        int port = config.getInteger("port", 8080);
        int instances = config.getInteger("instances", 1);

        log.info("Starting Thunder application on port {} with {} instance(s)", port, instances);

        HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setPort(port);

        return vertx.rxDeployVerticle(
                () -> new RestVerticle(httpServerOptions),
                new DeploymentOptions().setInstances(instances)
        ).ignoreElement();
    }

    private Integer getNumOfCores() {
        return CpuCoreSensor.availableProcessors();
    }
}

