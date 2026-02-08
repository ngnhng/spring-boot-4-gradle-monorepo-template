package com.onboard.provider.config.cache;

import com.onboard.infrastructure.core.config.PlatformProperties;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/** Configures Redis cache manager using cache entries from platform properties. */
@Configuration
@EnableCaching
public class CacheConfig {

  /**
   * Builds Redis cache manager with per-cache TTL overrides.
   *
   * @param redisConnectionFactory Redis connection factory
   * @param platformProperties platform configuration properties
   * @return Redis cache manager
   */
  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory redisConnectionFactory, PlatformProperties platformProperties) {
    RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig();
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    PlatformProperties.CacheProperties cacheProperties = platformProperties.getCache();
    if (cacheProperties != null && cacheProperties.getEntries() != null) {
      for (PlatformProperties.CacheProperties.CacheEntry entry : cacheProperties.getEntries()) {
        if (entry.getName() == null || entry.getName().isBlank()) {
          continue;
        }
        RedisCacheConfiguration cacheConfiguration = defaults.entryTtl(entry.getTtl());
        cacheConfigurations.put(entry.getName(), cacheConfiguration);
      }
    }

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaults)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
