package com.dream11.thunder.api;

import com.dream11.thunder.api.verticle.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for Thunder API application.
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting Thunder API application...");

        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);

        vertx.deployVerticle(new MainVerticle(), result -> {
            if (result.succeeded()) {
                log.info("Thunder API application started successfully");
                log.info("Deployment ID: {}", result.result());
            } else {
                log.error("Failed to start Thunder API application", result.cause());
                System.exit(1);
            }
        });
    }
}
