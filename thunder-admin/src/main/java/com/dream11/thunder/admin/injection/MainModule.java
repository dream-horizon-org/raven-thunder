package com.dream11.thunder.admin.injection;

import com.dream11.thunder.admin.service.AdminService;
import com.dream11.thunder.admin.service.BehaviourTagService;
import com.dream11.thunder.admin.service.admin.AdminServiceImpl;
import com.dream11.thunder.admin.service.behaviourTag.BehaviourTagServiceImpl;
import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.client.AerospikeClientImpl;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.config.Config;
import com.dream11.thunder.core.config.ServerConfig;
import com.dream11.thunder.core.dao.BehaviourTagsRepository;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.dao.NudgeRepository;
import com.dream11.thunder.core.dao.behaviourTag.BehaviourTagRepositoryImpl;
import com.dream11.thunder.core.dao.cta.CTARepositoryImpl;
import com.dream11.thunder.core.dao.nudge.NudgeRepositoryImpl;
import com.dream11.thunder.core.dao.nudge.preview.NudgePreviewRepositoryImpl;
import com.dream11.thunder.core.util.SharedDataUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MainModule extends AbstractModule {

    private final Vertx vertx;

    @Override
    protected void configure() {
        // Bind Vertx instances
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

        // Bind Aerospike Client
        bind(AerospikeClient.class).to(AerospikeClientImpl.class).in(Singleton.class);

        // Bind Repositories
        bind(CTARepository.class).to(CTARepositoryImpl.class).in(Singleton.class);
        bind(NudgeRepository.class).to(NudgeRepositoryImpl.class).in(Singleton.class);
        bind(BehaviourTagsRepository.class).to(BehaviourTagRepositoryImpl.class).in(Singleton.class);
        bind(NudgePreviewRepository.class).to(NudgePreviewRepositoryImpl.class).in(Singleton.class);

        // Bind Services
        bind(AdminService.class).to(AdminServiceImpl.class).in(Singleton.class);
        bind(BehaviourTagService.class).to(BehaviourTagServiceImpl.class).in(Singleton.class);

        log.info("MainModule configuration complete - all services and repositories bound");
    }
}
