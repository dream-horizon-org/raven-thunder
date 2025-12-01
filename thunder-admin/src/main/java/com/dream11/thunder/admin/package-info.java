@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(
    info = @org.eclipse.microprofile.openapi.annotations.info.Info(
        title = "Thunder Admin",
        version = "1.0.0",
        description = "Thunder Admin - Management endpoints"
    ),
    servers = {
        @org.eclipse.microprofile.openapi.annotations.servers.Server(
            url = "http://localhost:8081",
            description = "Thunder Admin Server (Management endpoints)"
        )
    }
)
package com.dream11.thunder.admin;