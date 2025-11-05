package com.dream11.thunder.injection;

import com.dream11.thunder.config.AerospikeConfig;
import com.dream11.thunder.config.ServerConfig;
import com.dream11.thunder.util.SharedDataUtils;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MainModule extends AbstractModule {

    private final Vertx vertx;

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(vertx);
        bind(io.vertx.rxjava3.core.Vertx.class)
                .toInstance(io.vertx.rxjava3.core.Vertx.newInstance(vertx));

        bind(ServerConfig.class).toProvider(() -> {
            JsonObject cfg = SharedDataUtils.get(vertx, JsonObject.class, "applicationConfig");
            JsonObject server = cfg != null ? cfg.getJsonObject("server", new JsonObject()) : new JsonObject();
            ServerConfig sc = new ServerConfig();
            sc.setHost(server.getString("host", sc.getHost()));
            sc.setPort(server.getInteger("port", sc.getPort()));
            sc.setInstances(server.getInteger("instances", sc.getInstances()));
            sc.setCompressionSupported(server.getBoolean("compressionSupported", sc.isCompressionSupported()));
            sc.setIdleTimeout(server.getInteger("idleTimeout", sc.getIdleTimeout()));
            return sc;
        });

        bind(AerospikeConfig.class).toProvider(() -> {
            JsonObject cfg = SharedDataUtils.get(vertx, JsonObject.class, "applicationConfig");
            JsonObject a = cfg != null ? cfg.getJsonObject("aerospike") : null;
            AerospikeConfig ac = new AerospikeConfig();
            if (a != null) {
                ac.setHosts(a.getString("hosts", ac.getHosts()));
                ac.setNamespace(a.getString("namespace", ac.getNamespace()));
                ac.setPort(a.getInteger("port", ac.getPort()));
                ac.setSocketTimeout(a.getInteger("socket-timeout", ac.getSocketTimeout()));
                ac.setTotalTimeout(a.getInteger("total-timeout", ac.getTotalTimeout()));
                ac.setMaxConnections(a.getInteger("max-connections", ac.getMaxConnections()));
            }
            return ac;
        });
    }
}

