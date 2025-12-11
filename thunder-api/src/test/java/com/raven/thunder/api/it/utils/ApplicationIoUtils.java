package com.raven.thunder.api.it.utils;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;

public final class ApplicationIoUtils {

  private ApplicationIoUtils() {}

  public static Response ping() {
    return given().get("/healthcheck/ping");
  }

  public static Response healthCheck() {
    return given().get("/healthcheck");
  }
}
