package com.dream11.thunder.api.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(Setup.class)
class SdkApiIT {

  @Test
  @DisplayName("POST /cta/active/state-machines/ without api_version returns empty CTAResponse")
  void appLaunch_returnsEmptyCTAResponse_whenApiVersionMissing() {
    given()
        .header("x-tenant-id", "default")
        .header("auth-userid", 123L)
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/cta/active/state-machines/")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("statusCode", equalTo(200))
        .body("data", notNullValue());
  }

  @Test
  @DisplayName("POST /cta/active/state-machines/ with api_version < 1 returns empty CTAResponse")
  void appLaunch_returnsEmptyCTAResponse_whenApiVersionZero() {
    given()
        .header("x-tenant-id", "default")
        .header("auth-userid", 123L)
        .header("api_version", 0)
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/cta/active/state-machines/")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("statusCode", equalTo(200))
        .body("data", notNullValue());
  }

  @Test
  @DisplayName(
      "POST /cta/active/state-machines/ missing auth-userid still returns 200 (no header validation)")
  void appLaunch_missingAuthUserId_returns200() {
    given()
        .header("x-tenant-id", "default")
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/cta/active/state-machines/")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("POST /cta/state-machines/snapshot/delta/ without api_version returns false")
  void merge_returnsFalse_whenApiVersionMissing() {
    given()
        .header("x-tenant-id", "default")
        .header("auth-userid", 123L)
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/cta/state-machines/snapshot/delta/")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("statusCode", equalTo(200))
        .body("data", equalTo(false));
  }

  @Test
  @DisplayName("POST /cta/state-machines/snapshot/delta/ with api_version < 1 returns false")
  void merge_returnsFalse_whenApiVersionZero() {
    given()
        .header("x-tenant-id", "default")
        .header("auth-userid", 123L)
        .header("api_version", 0)
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/cta/state-machines/snapshot/delta/")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("statusCode", equalTo(200))
        .body("data", equalTo(false));
  }

  @Test
  @DisplayName(
      "POST /cta/state-machines/snapshot/delta/ missing auth-userid still returns 200 (no header validation)")
  void merge_missingAuthUserId_returns200() {
    given()
        .header("x-tenant-id", "default")
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/cta/state-machines/snapshot/delta/")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("GET /cta/nudge/preview/{id} unknown id returns 200")
  void nudgePreview_unknownId_returns200() {
    given()
        .header("x-tenant-id", "default")
        .when()
        .get("/cta/nudge/preview/{id}", "unknown-id-123")
        .then()
        .statusCode(200);
  }
}
