package com.dream11.thunder.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AerospikeConfig {

    @JsonProperty("hosts")
    private String hosts;

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("port")
    private int port = 3000;

    @JsonProperty("socket-timeout")
    private int socketTimeout = 5000;

    @JsonProperty("total-timeout")
    private int totalTimeout = 10000;

    @JsonProperty("max-connections")
    private int maxConnections = 100;
}

