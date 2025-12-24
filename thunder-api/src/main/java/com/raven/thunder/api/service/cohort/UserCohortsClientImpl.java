package com.raven.thunder.api.service.cohort;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.raven.thunder.api.constant.Constants;
import com.raven.thunder.api.service.UserCohortsClient;
import com.raven.thunder.core.config.CohortConfig;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.ext.web.client.WebClient;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserCohortsClient that fetches cohorts from an external HTTP service. Uses
 * Vert.x WebClient with connection pooling for efficient HTTP requests.
 */
@Slf4j
@Singleton
public class UserCohortsClientImpl implements UserCohortsClient {

  private final WebClient webClient;
  private final CohortConfig cohortConfig;
  private final ObjectMapper objectMapper;
  private final String baseUrl;

  @Inject
  public UserCohortsClientImpl(Vertx vertx, CohortConfig cohortConfig) {
    this.cohortConfig = cohortConfig;
    this.objectMapper = new ObjectMapper();
    this.baseUrl =
        cohortConfig != null && cohortConfig.getUrl() != null
            ? cohortConfig.getUrl()
            : null;

    // Configure HTTP client with connection pooling
    HttpClientOptions httpClientOptions = new HttpClientOptions();

    if (cohortConfig != null) {
      // Use max-pool-size if provided, otherwise fall back to connection-pool-size
      if (cohortConfig.getMaxPoolSize() != null) {
        httpClientOptions.setMaxPoolSize(cohortConfig.getMaxPoolSize());
      } else if (cohortConfig.getConnectionPoolSize() != null) {
        httpClientOptions.setMaxPoolSize(cohortConfig.getConnectionPoolSize());
      }
      if (cohortConfig.getKeepAlive() != null) {
        httpClientOptions.setKeepAlive(cohortConfig.getKeepAlive());
      }
      if (cohortConfig.getKeepAliveTimeout() != null) {
        httpClientOptions.setKeepAliveTimeout(cohortConfig.getKeepAliveTimeout());
      }
      if (cohortConfig.getConnectTimeout() != null) {
        httpClientOptions.setConnectTimeout(cohortConfig.getConnectTimeout());
      }
      if (cohortConfig.getIdleTimeout() != null) {
        httpClientOptions.setIdleTimeout(cohortConfig.getIdleTimeout());
      }
      if (cohortConfig.getMaxWaitQueueSize() != null) {
        httpClientOptions.setMaxWaitQueueSize(cohortConfig.getMaxWaitQueueSize());
      }
    }

    // Set defaults if not configured
    if (httpClientOptions.getMaxPoolSize() == -1) {
      httpClientOptions.setMaxPoolSize(50);
    }
    if (httpClientOptions.getConnectTimeout() == -1) {
      httpClientOptions.setConnectTimeout(5000);
    }
    if (httpClientOptions.getIdleTimeout() == -1) {
      httpClientOptions.setIdleTimeout(60);
    }

    // Create HttpClient with the configured options, then create WebClient from it
    HttpClient httpClient = vertx.createHttpClient(httpClientOptions);
    this.webClient = WebClient.wrap(httpClient);

    log.info(
        "UserCohortsClientImpl initialized with URL: {}, maxPoolSize: {}, connectTimeout: {}ms",
        baseUrl,
        httpClientOptions.getMaxPoolSize(),
        httpClientOptions.getConnectTimeout());
  }

  @Override
  public Single<Set<String>> findAllCohorts(Long userId) {
    if (baseUrl == null || baseUrl.isEmpty()) {
      log.warn(
          "Cohort service URL not configured. Returning default cohort '{}' for userId: {}",
          Constants.DEFAULT_COHORT_ALL,
          userId);
      return Single.just(Set.of(Constants.DEFAULT_COHORT_ALL));
    }

    log.debug("Fetching cohorts for userId: {} from base URL: {}", userId, baseUrl);

    return webClient
        .getAbs(baseUrl)
        .addQueryParam(Constants.USER_ID_QUERY_PARAM, String.valueOf(userId))
        .rxSend()
        .map(
            response -> {
              if (response.statusCode() == 200) {
                try {
                  String body = response.bodyAsString();
                  log.debug("Received response for userId {}: {}", userId, body);

                  // Parse response as FindAllCohortsResponse with data field
                  FindAllCohortsResponse cohortResponse =
                      objectMapper.readValue(body, FindAllCohortsResponse.class);

                  // Convert List<String> to Set<String>
                  Set<String> cohorts =
                      cohortResponse.getData() != null
                          ? new HashSet<>(cohortResponse.getData())
                          : Collections.emptySet();

                  log.info(
                      "Successfully fetched {} cohorts for userId: {}", cohorts.size(), userId);
                  return cohorts;
                } catch (Exception e) {
                  log.error(
                      "Failed to parse cohort response for userId: {}. Response body: {}",
                      userId,
                      response.bodyAsString(),
                      e);
                  return Collections.<String>emptySet();
                }
              } else {
                log.warn(
                    "Cohort service returned status {} for userId: {}. Response: {}",
                    response.statusCode(),
                    userId,
                    response.bodyAsString());
                return Collections.<String>emptySet();
              }
            })
        .onErrorResumeNext(
            error -> {
              log.error(
                  "Error fetching cohorts for userId: {} from base URL: {}", userId, baseUrl, error);
              // Return empty set on error to allow the system to continue
              return Single.just(Collections.<String>emptySet());
            });
  }
}
