package com.raven.thunder.core.util;

import com.raven.thunder.core.io.Response;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Utility class to wrap RxJava3 reactive types into CompletionStage<Response<T>>. Used for
 * converting reactive streams to JAX-RS compatible responses.
 */
public class ResponseWrapper {

  /**
   * Converts Maybe to CompletionStage with Response wrapper.
   *
   * @param source The Maybe source
   * @param defaultValue Default value if Maybe is empty
   * @param httpStatusCode HTTP status code for response
   * @param <T> Type of data
   * @return CompletionStage with Response
   */
  public static <T> CompletionStage<Response<T>> fromMaybe(
      Maybe<T> source, T defaultValue, int httpStatusCode) {
    CompletableFuture<Response<T>> future = new CompletableFuture<>();
    source.subscribe(
        value -> future.complete(Response.successfulResponse(value, httpStatusCode)),
        error -> future.complete(Response.errorResponse(error, 500)),
        () -> future.complete(Response.successfulResponse(defaultValue, httpStatusCode)));
    return future;
  }

  /**
   * Converts Completable to CompletionStage with Response wrapper.
   *
   * @param source The Completable source
   * @param value Value to include in success response
   * @param httpStatusCode HTTP status code for response
   * @param <T> Type of data
   * @return CompletionStage with Response
   */
  public static <T> CompletionStage<Response<T>> fromCompletable(
      Completable source, T value, int httpStatusCode) {
    CompletableFuture<Response<T>> future = new CompletableFuture<>();
    source.subscribe(
        () -> future.complete(Response.successfulResponse(value, httpStatusCode)),
        error -> future.complete(Response.errorResponse(error, 500)));
    return future;
  }

  /**
   * Converts Single to CompletionStage with Response wrapper.
   *
   * @param source The Single source
   * @param httpStatusCode HTTP status code for response
   * @param <T> Type of data
   * @return CompletionStage with Response
   */
  public static <T> CompletionStage<Response<T>> fromSingle(Single<T> source, int httpStatusCode) {
    CompletableFuture<Response<T>> future = new CompletableFuture<>();
    source.subscribe(
        value -> future.complete(Response.successfulResponse(value, httpStatusCode)),
        error -> future.complete(Response.errorResponse(error, 500)));
    return future;
  }
}
