package com.onboard.infrastructure.core.idempotency;

import java.time.Duration;
import java.util.function.Supplier;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

/** Shared idempotency logic for cache-backed implementations. */
public abstract class AbstractIdempotencyService implements IdempotencyService {

  private static final Duration DEFAULT_TTL = Duration.ofSeconds(60);

  private final ObjectMapper objectMapper;

  /**
   * Creates a new cache-backed idempotency service.
   *
   * @param objectMapper serializer used for cached responses
   */
  protected AbstractIdempotencyService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <T> T execute(
      String namespace, String key, Duration ttl, JavaType returnType, Supplier<T> supplier) {
    if (!StringUtils.hasText(key)) {
      return supplier.get();
    }

    String cacheKey = buildCacheKey(namespace, key);
    String cachedValue = getCached(cacheKey);
    if (cachedValue != null) {
      return deserialize(cachedValue, returnType);
    }

    T result = supplier.get();
    if (result != null) {
      putCached(cacheKey, serialize(result), normalizeTtl(ttl));
    }
    return result;
  }

  /**
   * Reads a cached payload for the given key.
   *
   * @param cacheKey storage key
   * @return cached payload, or {@code null} when absent
   */
  protected abstract String getCached(String cacheKey);

  /**
   * Stores a serialized payload for the given key.
   *
   * @param cacheKey storage key
   * @param payload serialized response payload
   * @param ttl entry time to live
   */
  protected abstract void putCached(String cacheKey, String payload, Duration ttl);

  private static String buildCacheKey(String namespace, String key) {
    String resolvedNamespace = StringUtils.hasText(namespace) ? namespace : "default";
    return "idempotency:" + resolvedNamespace + ":" + key;
  }

  private Duration normalizeTtl(Duration ttl) {
    if (ttl == null || ttl.isNegative() || ttl.isZero()) {
      return DEFAULT_TTL;
    }
    return ttl;
  }

  private String serialize(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to serialize idempotent response", ex);
    }
  }

  private <T> T deserialize(String payload, JavaType returnType) {
    try {
      return objectMapper.readValue(payload, returnType);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to deserialize idempotent response", ex);
    }
  }
}
