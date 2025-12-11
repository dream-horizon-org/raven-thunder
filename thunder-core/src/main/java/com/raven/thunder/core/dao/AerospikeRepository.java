package com.raven.thunder.core.dao;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.policy.*;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.Statement;
import com.raven.thunder.core.client.AerospikeClient;
import com.raven.thunder.core.config.AerospikeConfig;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AerospikeRepository implements AerospikeOperations {

  protected final AerospikeClient client;
  protected final AerospikeConfig config;

  protected final Policy defaultReadPolicy = new Policy();
  protected final WritePolicy defaultWritePolicy = new WritePolicy();
  protected final QueryPolicy defaultQueryPolicy = new QueryPolicy();

  protected AerospikeRepository(AerospikeConfig config, AerospikeClient client) {
    this.client = client;
    this.config = config;

    setDefaultReadPolicyParams(defaultReadPolicy, config);
    setDefaultWritePolicyParams(defaultWritePolicy, config);
    setDefaultQueryPolicyParams(defaultQueryPolicy, config);
  }

  protected void setDefaultReadPolicyParams(Policy readPolicy, AerospikeConfig config) {
    readPolicy.socketTimeout = Math.toIntExact(config.getSocketTimeoutInterval().getMs());
    readPolicy.totalTimeout = Math.toIntExact(config.getTotalTimeoutInterval().getMs());
    readPolicy.replica = Replica.SEQUENCE;
  }

  protected void setDefaultWritePolicyParams(WritePolicy writePolicy, AerospikeConfig config) {
    writePolicy.socketTimeout = Math.toIntExact(config.getSocketTimeoutInterval().getMs());
    writePolicy.totalTimeout = Math.toIntExact(config.getTotalTimeoutInterval().getMs());
    writePolicy.commitLevel = CommitLevel.COMMIT_MASTER;
  }

  protected void setDefaultQueryPolicyParams(QueryPolicy queryPolicy, AerospikeConfig config) {
    queryPolicy.socketTimeout = Math.toIntExact(config.getBulkReadSocketTimeoutInterval().getMs());
    queryPolicy.totalTimeout = Math.toIntExact(config.getBulkReadSocketTimeoutInterval().getMs());
  }

  @Override
  public Maybe<Record> find(Policy readPolicy, Key pk) {
    return client.rxGet(readPolicy, pk).filter(record -> record != null);
  }

  @Override
  public <T> Maybe<T> find(Policy readPolicy, Key pk, Function<Record, T> recordMapper) {
    return find(readPolicy, pk).map(recordMapper);
  }

  @Override
  public Maybe<Record> find(Key pk) {
    return find(defaultReadPolicy, pk);
  }

  @Override
  public <T> Maybe<T> find(Key pk, Function<Record, T> recordMapper) {
    return find(defaultReadPolicy, pk, recordMapper);
  }

  @Override
  public Maybe<Record> find(Policy readPolicy, Key pk, String... bins) {
    return client
        .rxGet(readPolicy, pk, bins)
        .filter(record -> record != null && record.bins != null);
  }

  @Override
  public <T> Maybe<T> find(
      Policy readPolicy, Key pk, Function<Record, T> recordMapper, String... bins) {
    return find(readPolicy, pk, bins).map(recordMapper);
  }

  @Override
  public Maybe<Record> find(Key pk, String... bins) {
    return find(defaultReadPolicy, pk, bins);
  }

  @Override
  public <T> Maybe<T> find(Key pk, Function<Record, T> recordMapper, String... bins) {
    return find(defaultReadPolicy, pk, recordMapper, bins);
  }

  @Override
  public Single<List<KeyRecord>> findAll(QueryPolicy queryPolicy, Statement statement) {
    return client.rxQuery(queryPolicy, statement);
  }

  @Override
  public <K, V> Single<Map<K, V>> findAll(
      QueryPolicy queryPolicy, Statement statement, Function<KeyRecord, V> keyRecordMapper) {
    return findAll(queryPolicy, statement)
        .map(
            keyRecords -> {
              Map<K, V> result = new HashMap<>();
              for (KeyRecord keyRecord : keyRecords) {
                result.put((K) keyRecord.key.userKey.toString(), keyRecordMapper.apply(keyRecord));
              }
              return result;
            });
  }

  @Override
  public Single<List<KeyRecord>> findAll(Statement statement) {
    return findAll(defaultQueryPolicy, statement);
  }

  @Override
  public <K, V> Single<Map<K, V>> findAll(
      Statement statement, Function<KeyRecord, V> keyRecordMapper) {
    return findAll(defaultQueryPolicy, statement, keyRecordMapper);
  }

  @Override
  public Single<Key> upsert(WritePolicy writePolicy, Key pk, Bin... bins) {
    return client.rxPut(writePolicy, pk, bins);
  }

  @Override
  public Single<Key> upsert(Key pk, Bin... bins) {
    return upsert(defaultWritePolicy, pk, bins);
  }

  @Override
  public Single<Record> operate(WritePolicy writePolicy, Key key, Operation[] operations) {
    return client.rxOperate(writePolicy, key, operations);
  }
}
