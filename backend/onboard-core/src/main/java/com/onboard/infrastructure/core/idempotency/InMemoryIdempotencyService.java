package com.onboard.infrastructure.core.idempotency;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tools.jackson.databind.ObjectMapper;

/** Fallback in-memory implementation when Redis is not available. */
public class InMemoryIdempotencyService extends AbstractIdempotencyService {

  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

  /**
   * Creates an in-memory idempotency service.
   *
   * @param objectMapper serializer used for cached responses
   */
  public InMemoryIdempotencyService(ObjectMapper objectMapper) {
    super(objectMapper);
  }

  @Override
  protected String getCached(String cacheKey) {
    CacheEntry cacheEntry = cache.get(cacheKey);
    if (cacheEntry == null) {
      return null;
    }
    if (System.currentTimeMillis() > cacheEntry.expiresAtMillis()) {
      cache.remove(cacheKey);
      return null;
    }
    return cacheEntry.payload();
  }

  @Override
  protected void putCached(String cacheKey, String payload, Duration ttl) {
    long expiresAtMillis = System.currentTimeMillis() + ttl.toMillis();
    cache.put(cacheKey, new CacheEntry(payload, expiresAtMillis));
  }

  private record CacheEntry(String payload, long expiresAtMillis) {}
}
