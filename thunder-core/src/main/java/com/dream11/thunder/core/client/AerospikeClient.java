package com.dream11.thunder.core.client;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.Statement;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

public interface AerospikeClient {
    Completable rxConnect();
    Completable rxClose();
    com.aerospike.client.AerospikeClient getClient();
    boolean isConnected();

    // Reactive Aerospike operations
    Maybe<Record> rxGet(Policy policy, Key key);
    Maybe<Record> rxGet(Policy policy, Key key, String... bins);
    Single<Key> rxPut(WritePolicy writePolicy, Key key, Bin... bins);
    Single<Record> rxOperate(WritePolicy writePolicy, Key key, Operation... operations);
    Single<List<KeyRecord>> rxQuery(QueryPolicy queryPolicy, Statement statement);
}
