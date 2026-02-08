package com.onboard.infrastructure.web;

import java.time.Instant;

/** Standard API response envelope. */
public record ApiResponse<T>(boolean success, String message, T data, Instant timestamp) {
  /** Creates a successful response payload. */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "success", data, Instant.now());
  }

  /** Creates a successful created response payload. */
  public static <T> ApiResponse<T> created(T data) {
    return new ApiResponse<>(true, "created", data, Instant.now());
  }
}
