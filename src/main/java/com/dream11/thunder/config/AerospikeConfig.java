package com.dream11.thunder.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aerospike configuration class.
 * Contains all Aerospike connection settings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AerospikeConfig {
    
    @JsonProperty("hosts")
    private String hosts;
    
    @JsonProperty("namespace")
    private String namespace;
    
    @JsonProperty("port")
    private Integer port;
    
    @JsonProperty("socket-timeout")
    private Integer socketTimeout;
    
    @JsonProperty("total-timeout")
    private Integer totalTimeout;
    
    @JsonProperty("max-connections")
    private Integer maxConnections;
}

