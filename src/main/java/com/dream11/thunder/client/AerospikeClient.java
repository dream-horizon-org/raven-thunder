package com.dream11.thunder.client;

import io.reactivex.rxjava3.core.Completable;

public interface AerospikeClient {
    Completable rxConnect();
    Completable rxClose();
    com.aerospike.client.AerospikeClient getClient();
    boolean isConnected();
}
