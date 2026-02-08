package com.onboard.infrastructure.core.idempotency;

import java.time.Duration;
import java.util.function.Supplier;
import tools.jackson.databind.JavaType;

/** Base idempotency service contract supporting explicit service-based usage. */
public interface IdempotencyService {

  /**
   * Executes the supplier exactly once per idempotency key within the TTL and returns cached value
   * on retries.
   */
  <T> T execute(
      String namespace, String key, Duration ttl, JavaType returnType, Supplier<T> supplier);
}
