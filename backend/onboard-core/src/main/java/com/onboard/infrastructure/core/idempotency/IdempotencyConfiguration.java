package com.onboard.infrastructure.core.idempotency;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

/** Registers idempotency service with Redis-first fallback to in-memory storage. */
@Configuration
public class IdempotencyConfiguration {

  /**
   * Registers the Redis-backed idempotency service when Redis is available.
   *
   * @param objectMapper serializer used for cached responses
   * @param stringRedisTemplate Redis template for value operations
   * @return Redis-backed idempotency service
   */
  @Bean
  @ConditionalOnBean(StringRedisTemplate.class)
  public IdempotencyService idempotencyService(
      ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
    return new RedisIdempotencyService(stringRedisTemplate, objectMapper);
  }

  /**
   * Registers an in-memory fallback idempotency service.
   *
   * @param objectMapper serializer used for cached responses
   * @return in-memory idempotency service
   */
  @Bean
  @ConditionalOnMissingBean(IdempotencyService.class)
  public IdempotencyService inMemoryIdempotencyService(ObjectMapper objectMapper) {
    return new InMemoryIdempotencyService(objectMapper);
  }
}
