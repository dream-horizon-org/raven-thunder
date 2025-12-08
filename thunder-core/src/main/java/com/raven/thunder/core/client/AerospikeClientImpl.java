package com.raven.thunder.core.client;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.google.inject.Inject;
import com.raven.thunder.core.config.AerospikeConfig;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AerospikeClientImpl implements AerospikeClient {

  private com.aerospike.client.AerospikeClient client;
  private final AerospikeConfig config;

  @Override
  public Completable rxConnect() {
    return Completable.fromAction(
            () -> {
              try {
                ClientPolicy policy = new ClientPolicy();
                policy.timeout = config.getTotalTimeout();
                policy.maxConnsPerNode = config.getMaxConnections();

                String[] hostPorts = config.getHost().split(",");
                com.aerospike.client.Host[] hosts =
                    Arrays.stream(hostPorts)
                        .map(
                            hp -> {
                              String[] parts = hp.trim().split(":");
                              if (parts.length == 2) {
                                return new com.aerospike.client.Host(
                                    parts[0].trim(), Integer.parseInt(parts[1].trim()));
                              } else {
                                return new com.aerospike.client.Host(
                                    parts[0].trim(), config.getPort());
                              }
                            })
                        .toArray(com.aerospike.client.Host[]::new);

                this.client = new com.aerospike.client.AerospikeClient(policy, hosts);
                log.info("Aerospike client connected successfully to host: {}", config.getHost());
              } catch (Exception e) {
                log.error("Failed to connect to Aerospike", e);
                throw new RuntimeException("Failed to connect to Aerospike", e);
              }
            })
        .doOnComplete(() -> log.info("Aerospike client initialization completed"));
  }

  @Override
  public Completable rxClose() {
    return Completable.fromAction(
        () -> {
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

  @Override
  public Maybe<Record> rxGet(Policy policy, Key key) {
    return Maybe.fromCallable(() -> getClient().get(policy, key));
  }

  @Override
  public Maybe<Record> rxGet(Policy policy, Key key, String... bins) {
    return Maybe.fromCallable(() -> getClient().get(policy, key, bins));
  }

  @Override
  public Single<Key> rxPut(WritePolicy writePolicy, Key key, Bin... bins) {
    return Single.fromCallable(
        () -> {
          getClient().put(writePolicy, key, bins);
          return key;
        });
  }

  @Override
  public Single<Record> rxOperate(WritePolicy writePolicy, Key key, Operation... operations) {
    return Single.fromCallable(() -> getClient().operate(writePolicy, key, operations));
  }

  @Override
  public Single<List<KeyRecord>> rxQuery(QueryPolicy queryPolicy, Statement statement) {
    return Single.fromCallable(
        () -> {
          List<KeyRecord> results = new ArrayList<>();
          try (RecordSet recordSet = getClient().query(queryPolicy, statement)) {
            while (recordSet.next()) {
              KeyRecord keyRecord = new KeyRecord(recordSet.getKey(), recordSet.getRecord());
              results.add(keyRecord);
            }
          }
          return results;
        });
  }
}
