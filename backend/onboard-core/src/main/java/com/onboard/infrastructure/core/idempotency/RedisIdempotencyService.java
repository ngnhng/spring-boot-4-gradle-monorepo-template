package com.onboard.infrastructure.core.idempotency;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

/** Redis-backed implementation for idempotency response storage. */
public class RedisIdempotencyService extends AbstractIdempotencyService {

  private final StringRedisTemplate stringRedisTemplate;

  /**
   * Creates a Redis-backed idempotency service.
   *
   * @param stringRedisTemplate Redis template for value operations
   * @param objectMapper serializer used for cached responses
   */
  public RedisIdempotencyService(
      StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
    super(objectMapper);
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected String getCached(String cacheKey) {
    return stringRedisTemplate.opsForValue().get(cacheKey);
  }

  @Override
  protected void putCached(String cacheKey, String payload, Duration ttl) {
    stringRedisTemplate.opsForValue().set(cacheKey, payload, ttl);
  }
}
