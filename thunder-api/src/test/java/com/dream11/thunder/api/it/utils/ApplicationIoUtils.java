package com.dream11.thunder.api.it.utils;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class ApplicationIoUtils {

	private ApplicationIoUtils() {}

	public static Response ping() {
		return given().get("/healthcheck/ping");
	}

	public static Response healthCheck() {
		return given().get("/healthcheck");
	}
}


