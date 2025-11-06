package com.dream11.thunder.api.config;

import com.dream11.thunder.core.config.AerospikeConfig;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class AppConfig {

  private AerospikeConfig aerospike;
  private UserCohortsConfig userCohorts;
  private CacheConfig cache;
}
