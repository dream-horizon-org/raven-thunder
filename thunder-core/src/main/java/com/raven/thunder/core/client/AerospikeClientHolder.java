package com.raven.thunder.core.client;

public final class AerospikeClientHolder {

  private static volatile AerospikeClient instance;

  private AerospikeClientHolder() {}

  public static void set(AerospikeClient client) {
    instance = client;
  }

  public static AerospikeClient get() {
    return instance;
  }

  public static void clear() {
    instance = null;
  }
}
