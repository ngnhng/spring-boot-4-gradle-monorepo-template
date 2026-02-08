package com.onboard.infrastructure.core.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Root configuration properties for the onboard platform. */
@Getter
@Setter
@ConfigurationProperties(prefix = "platform")
public class PlatformProperties {
  private String nodeId;
  private String idempotencyKeyHeaderName;

  private Boolean insecureHttpClient;
  private long clientConnectTimeout;
  private long clientReadTimeout;
  private long clientWriteTimeout;

  private CorsProperties cors;
  private SecurityProperties security;
  private CacheProperties cache;
  private MessagingProperties messaging;

  /** CORS configuration applied to inbound HTTP endpoints. */
  @Getter
  @Setter
  public static class CorsProperties {

    private boolean enabled;
    private List<String> allowedOriginPatterns;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private boolean allowCredentials;
  }

  /** Security-related platform properties. */
  @Getter
  @Setter
  public static class SecurityProperties {

    private SecurityBasicAuth basicauth;
    private SecurityTwoFactorAuth twoFactor;
    private SecurityHsts hsts;
    private SecurityOauth2Properties oauth2;
    private CorsProperties cors;

    /** Binds the legacy `2fa` property alias to `twoFactor`. */
    public void set2fa(SecurityTwoFactorAuth twoFactor) {
      this.twoFactor = twoFactor;
    }

    /** OAuth2 resource server and client settings. */
    @Getter
    @Setter
    public static class SecurityOauth2Properties {

      private boolean enabled;
      private ClientProperties client;

      /** OAuth2 client registrations grouped by registration id. */
      @Getter
      @Setter
      public static class ClientProperties {

        private Map<String, Registration> registrations = new HashMap<>();

        /** Individual OAuth2 client registration settings. */
        @Getter
        @Setter
        public static final class Registration {

          private String clientId;
          private List<String> scopes = new ArrayList<>();
          private List<String> authorizationGrantTypes = new ArrayList<>();
          private List<String> redirectUris = new ArrayList<>();
          private boolean requireAuthorizationConsent = true;
        }
      }
    }

    /** Basic authentication toggle. */
    @Getter
    @Setter
    public static class SecurityBasicAuth {

      private boolean enabled;
    }

    /** Two-factor authentication toggle. */
    @Getter
    @Setter
    public static class SecurityTwoFactorAuth {

      private boolean enabled;
    }

    /** HSTS configuration toggle. */
    @Getter
    @Setter
    public static class SecurityHsts {

      private boolean enabled;
    }
  }

  /** Cache names and cache policies. */
  @Getter
  @Setter
  public static class CacheProperties {
    private List<CacheEntry> entries = new ArrayList<>();

    /** Individual cache definition. */
    @Getter
    @Setter
    public static class CacheEntry {
      private String name;
      private Duration ttl = Duration.ofMinutes(10);
    }
  }

  /** Messaging-related properties. */
  @Getter
  @Setter
  public static class MessagingProperties {
    private RabbitProperties rabbit;

    /** RabbitMQ topology configuration. */
    @Getter
    @Setter
    public static class RabbitProperties {
      private List<Exchange> exchanges = new ArrayList<>();
      private List<Queue> queues = new ArrayList<>();
      private List<Binding> bindings = new ArrayList<>();

      /** Exchange definition. */
      @Getter
      @Setter
      public static class Exchange {
        private String name;
        private String type = "topic";
        private boolean durable = true;
        private boolean autoDelete = false;
      }

      /** Queue definition. */
      @Getter
      @Setter
      public static class Queue {
        private String name;
        private boolean durable = true;
        private boolean quorum = false;
        private String deadLetterExchange;
        private String deadLetterRoutingKey;
      }

      /** Binding definition between exchange and queue. */
      @Getter
      @Setter
      public static class Binding {
        private String exchange;
        private String queue;
        private String routingKey = "";
      }
    }
  }
}
