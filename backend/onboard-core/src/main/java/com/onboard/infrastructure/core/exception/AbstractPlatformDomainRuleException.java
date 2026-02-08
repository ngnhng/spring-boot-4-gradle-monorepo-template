package com.onboard.infrastructure.core.exception;

/**
 * A {@link RuntimeException} thrown when a valid API request ends up violating some domain rule.
 */
public abstract class AbstractPlatformDomainRuleException extends AbstractPlatformException {

  /** Creates a domain-rule exception with an i18n message code and default user message. */
  protected AbstractPlatformDomainRuleException(String i18nMessageCode, String defaultUserMessage) {
    super(i18nMessageCode, defaultUserMessage);
  }

  /** Creates a domain-rule exception with a cause. */
  protected AbstractPlatformDomainRuleException(
      String i18nMessageCode, String defaultUserMessage, Throwable cause) {
    super(i18nMessageCode, defaultUserMessage, cause);
  }

  /** Creates a domain-rule exception with user message template arguments. */
  protected AbstractPlatformDomainRuleException(
      String i18nMessageCode, String defaultUserMessage, Object... defaultUserMessageArgs) {
    super(i18nMessageCode, defaultUserMessage, defaultUserMessageArgs);
  }
}
