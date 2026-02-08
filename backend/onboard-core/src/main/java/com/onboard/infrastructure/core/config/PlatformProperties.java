package com.onboard.infrastructure.core.config;

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
@ConfigurationProperties(prefix = "onboard")
public class PlatformProperties {
  private String nodeId;
  private String idempotencyKeyHeaderName;

  private Boolean insecureHttpClient;
  private long clientConnectTimeout;
  private long clientReadTimeout;
  private long clientWriteTimeout;

  private CorsProperties cors;
  private SecurityProperties security;

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
}
