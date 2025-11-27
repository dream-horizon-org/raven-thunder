package com.dream11.thunder.api.it;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@ExtendWith(Setup.class)
class HealthCheckIT {

	@Test
	@DisplayName("GET /healthcheck/ping should return 200 OK with 'pong'")
	void testPing() {
		given()
			.get("/healthcheck/ping")
			.then()
			.statusCode(200)
			.body(equalTo("pong"));
	}

	@Test
	@DisplayName("GET /healthcheck should return status response JSON")
	void testHealthCheck() {
		Response response = given().get("/healthcheck");
		response.then()
			.statusCode(200)
			.body("status", equalTo("UP"))
			.body("service", equalTo("thunder"))
			.body("aerospike", notNullValue())
			.body("aerospike.status", anyOf(equalTo("UP"), equalTo("DOWN")));
	}
}


