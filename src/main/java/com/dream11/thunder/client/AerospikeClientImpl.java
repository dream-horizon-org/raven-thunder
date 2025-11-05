package com.dream11.thunder.client;

import com.aerospike.client.policy.ClientPolicy;
import com.dream11.thunder.config.AerospikeConfig;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AerospikeClientImpl implements AerospikeClient {

    private com.aerospike.client.AerospikeClient client;
    private final AerospikeConfig config;

    @Override
    public Completable rxConnect() {
        return Completable.fromAction(() -> {
            try {
                ClientPolicy policy = new ClientPolicy();
                policy.timeout = config.getTotalTimeout();
                policy.maxConnsPerNode = config.getMaxConnections();

                String[] hostPorts = config.getHost().split(",");
                com.aerospike.client.Host[] hosts = Arrays.stream(hostPorts)
                        .map(hp -> {
                            String[] parts = hp.trim().split(":");
                            if (parts.length == 2) {
                                return new com.aerospike.client.Host(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                            } else {
                                return new com.aerospike.client.Host(parts[0].trim(), config.getPort());
                            }
                        })
                        .toArray(com.aerospike.client.Host[]::new);

                this.client = new com.aerospike.client.AerospikeClient(policy, hosts);
                log.info("Aerospike client connected successfully to host: {}", config.getHost());
            } catch (Exception e) {
                log.error("Failed to connect to Aerospike", e);
                throw new RuntimeException("Failed to connect to Aerospike", e);
            }
        }).doOnComplete(() -> log.info("Aerospike client initialization completed"));
    }

    @Override
    public Completable rxClose() {
        return Completable.fromAction(() -> {
            if (client != null) {
                client.close();
                log.info("Aerospike client closed");
            }
        });
    }

    @Override
    public com.aerospike.client.AerospikeClient getClient() {
        if (client == null) {
            throw new IllegalStateException("Aerospike client is not initialized");
        }
        return client;
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }
}
