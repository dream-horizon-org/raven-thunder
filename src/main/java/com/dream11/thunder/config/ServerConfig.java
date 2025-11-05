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
public class ServerConfig {

    @JsonProperty("host")
    private String host = "0.0.0.0";

    @JsonProperty("port")
    private int port = 8080;

    @JsonProperty("instances")
    private int instances = 1;

    @JsonProperty("compressionSupported")
    private boolean compressionSupported = true;

    @JsonProperty("idleTimeout")
    private int idleTimeout = 60;
}



