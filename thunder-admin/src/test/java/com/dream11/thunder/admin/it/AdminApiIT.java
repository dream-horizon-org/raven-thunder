package com.dream11.thunder.admin.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(Setup.class)
class AdminApiIT {

	@Test
	@DisplayName("GET /thunder/ctas/ returns 200")
	void getCtas_returns200() {
		given()
			.header("x-tenant-id", "default")
		.when()
			.get("/thunder/ctas/")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("GET /thunder/filters/ returns 200")
	void getFilters_returns200() {
		given()
			.header("x-tenant-id", "default")
		.when()
			.get("/thunder/filters/")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("GET /thunder/behaviour-tags/ returns 200")
	void getBehaviourTags_returns200() {
		given()
			.header("x-tenant-id", "default")
		.when()
			.get("/thunder/behaviour-tags/")
		.then()
			.statusCode(200);
	}
}


