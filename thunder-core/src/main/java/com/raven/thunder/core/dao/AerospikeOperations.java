package com.raven.thunder.core.dao;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.Statement;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import java.util.List;
import java.util.Map;

public interface AerospikeOperations {
  Maybe<Record> find(Policy readPolicy, Key pk);

  <T> Maybe<T> find(Policy readPolicy, Key pk, Function<Record, T> recordMapper);

  Maybe<Record> find(Key pk);

  <T> Maybe<T> find(Key pk, Function<Record, T> recordMapper);

  Maybe<Record> find(Policy readPolicy, Key pk, String... bins);

  <T> Maybe<T> find(Policy readPolicy, Key pk, Function<Record, T> recordMapper, String... bins);

  Maybe<Record> find(Key pk, String... bins);

  <T> Maybe<T> find(Key pk, Function<Record, T> recordMapper, String... bins);

  Single<List<KeyRecord>> findAll(QueryPolicy queryPolicy, Statement statement);

  <K, V> Single<Map<K, V>> findAll(
      QueryPolicy queryPolicy, Statement statement, Function<KeyRecord, V> keyRecordMapper);

  Single<List<KeyRecord>> findAll(Statement statement);

  <K, V> Single<Map<K, V>> findAll(Statement statement, Function<KeyRecord, V> keyRecordMapper);

  Single<Key> upsert(WritePolicy writePolicy, Key pk, Bin... bins);

  Single<Key> upsert(Key pk, Bin... bins);

  Single<Record> operate(WritePolicy writePolicy, Key key, Operation[] operations);
}
