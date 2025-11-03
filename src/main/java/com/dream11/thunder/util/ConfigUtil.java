package com.dream11.thunder.util;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUtil {

    private static final String DEFAULT_CONFIG_FILE = "thunder-default.conf";

    public static ConfigRetriever getConfigRetriever(Vertx vertx) {
        ConfigStoreOptions defaultStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(new JsonObject().put("path", DEFAULT_CONFIG_FILE));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(defaultStore);

        return ConfigRetriever.create(vertx, options);
    }

    public static io.reactivex.rxjava3.core.Single<JsonObject> getConfig(Vertx vertx) {
        return getConfigRetriever(vertx).rxGetConfig()
                .doOnSuccess(config -> log.info("Configuration loaded successfully"))
                .doOnError(error -> log.error("Failed to load configuration", error));
    }
}

