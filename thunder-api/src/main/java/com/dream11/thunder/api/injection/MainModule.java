package com.dream11.thunder.api.injection;

import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.config.Config;
import com.dream11.thunder.core.config.ServerConfig;
import com.dream11.thunder.core.util.SharedDataUtils;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MainModule extends AbstractModule {

    private final Vertx vertx;

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(vertx);
        bind(io.vertx.rxjava3.core.Vertx.class)
                .toInstance(io.vertx.rxjava3.core.Vertx.newInstance(vertx));

        // Bind Config from shared data
        bind(Config.class).toProvider(() -> {
            Config config = SharedDataUtils.get(vertx, Config.class);
            if (config == null) {
                log.error("Config not found in SharedData!");
                throw new IllegalStateException("Config must be initialized in MainVerticle before Guice injection");
            }
            log.info("Config retrieved from SharedData for injection");
            return config;
        });

        // Bind ServerConfig from Config
        bind(ServerConfig.class).toProvider(() -> {
            Config config = SharedDataUtils.get(vertx, Config.class);
            if (config == null || config.getServer() == null) {
                log.error("ServerConfig not available!");
                throw new IllegalStateException("ServerConfig must be initialized");
            }
            return config.getServer();
        });

        // Bind AerospikeConfig from Config
        bind(AerospikeConfig.class).toProvider(() -> {
            Config config = SharedDataUtils.get(vertx, Config.class);
            if (config == null || config.getAerospike() == null) {
                log.warn("AerospikeConfig not available, returning null");
                return null;
            }
            return config.getAerospike();
        });
    }
}

