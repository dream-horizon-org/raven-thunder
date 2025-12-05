package com.dream11.thunder.core.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic REST API response wrapper.
 *
 * @param <T> Type of the response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

  private boolean success;
  private T data;
  private String error;
  private String message;
  private Integer statusCode;

  /**
   * Create a successful response with data.
   *
   * @param data The response data
   * @param statusCode HTTP status code
   * @param <T> Type of data
   * @return Response object
   */
  public static <T> Response<T> successfulResponse(T data, int statusCode) {
    return Response.<T>builder().success(true).data(data).statusCode(statusCode).build();
  }

  /**
   * Create a successful response with message.
   *
   * @param message The success message
   * @param statusCode HTTP status code
   * @param <T> Type of data
   * @return Response object
   */
  public static <T> Response<T> successfulResponse(String message, int statusCode) {
    return Response.<T>builder().success(true).message(message).statusCode(statusCode).build();
  }

  /**
   * Create an error response.
   *
   * @param error The error message
   * @param statusCode HTTP status code
   * @param <T> Type of data
   * @return Response object
   */
  public static <T> Response<T> errorResponse(String error, int statusCode) {
    return Response.<T>builder().success(false).error(error).statusCode(statusCode).build();
  }

  /**
   * Create an error response with exception.
   *
   * @param exception The exception
   * @param statusCode HTTP status code
   * @param <T> Type of data
   * @return Response object
   */
  public static <T> Response<T> errorResponse(Throwable exception, int statusCode) {
    return Response.<T>builder()
        .success(false)
        .error(exception.getMessage())
        .statusCode(statusCode)
        .build();
  }
}
