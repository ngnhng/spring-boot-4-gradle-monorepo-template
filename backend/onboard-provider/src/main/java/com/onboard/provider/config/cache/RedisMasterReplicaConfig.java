package com.onboard.provider.config.cache;

import io.lettuce.core.ReadFrom;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/** Configures Redis master-replica connectivity for high availability mode. */
@Configuration
@EnableConfigurationProperties(RedisMasterReplicaConfig.MasterReplicaProperties.class)
@ConditionalOnProperty(prefix = "platform.redis.ha", name = "mode", havingValue = "master-replica")
public class RedisMasterReplicaConfig {

  /**
   * Creates a Lettuce connection factory with replica-preferred reads.
   *
   * @param properties master-replica connection properties
   * @return Redis connection factory
   */
  @Bean
  public LettuceConnectionFactory redisConnectionFactory(MasterReplicaProperties properties) {
    RedisStaticMasterReplicaConfiguration redisConfiguration =
        new RedisStaticMasterReplicaConfiguration(
            properties.getMaster().getHost(), properties.getMaster().getPort());

    for (Node replica : properties.getReplicas()) {
      redisConfiguration.addNode(replica.getHost(), replica.getPort());
    }

    if (properties.getPassword() != null && !properties.getPassword().isBlank()) {
      redisConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
    }

    LettuceClientConfiguration clientConfiguration =
        LettuceClientConfiguration.builder().readFrom(ReadFrom.REPLICA_PREFERRED).build();

    return new LettuceConnectionFactory(redisConfiguration, clientConfiguration);
  }

  /** Externalized master-replica Redis settings. */
  @Getter
  @Setter
  @ConfigurationProperties(prefix = "platform.redis.ha")
  public static class MasterReplicaProperties {
    private String mode;
    private Node master = new Node();
    private List<Node> replicas = new ArrayList<>();
    private String password;
  }

  /** Host and port definition for a Redis node. */
  @Getter
  @Setter
  public static class Node {
    private String host;
    private int port;
  }
}
