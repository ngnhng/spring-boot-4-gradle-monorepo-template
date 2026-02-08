package com.onboard.infrastructure.core.exception;

import java.util.ArrayList;
import java.util.List;

/** Exception with internationalization support (it's message can be translated). */
public abstract class AbstractPlatformException extends RuntimeException {

  private static final Object[] NO_ARGS = new Object[0];

  private final String i18nMessageCode;
  private final String defaultUserMessage;
  private final Object[] defaultUserMessageArgs;

  /** Creates an exception with a translatable message code and default message. */
  protected AbstractPlatformException(String i18nMessageCode, String defaultUserMessage) {
    this(i18nMessageCode, defaultUserMessage, null, NO_ARGS);
  }

  /** Creates an exception with a translatable message code, default message, and cause. */
  protected AbstractPlatformException(
      String i18nMessageCode, String defaultUserMessage, Throwable cause) {
    this(i18nMessageCode, defaultUserMessage, cause, NO_ARGS);
  }

  /** Creates an exception with templated message arguments and optional embedded cause. */
  protected AbstractPlatformException(
      String i18nMessageCode, String defaultUserMessage, Object... defaultUserMessageArgs) {
    this(
        i18nMessageCode,
        defaultUserMessage,
        findThrowableCause(defaultUserMessageArgs),
        filterThrowableCause(defaultUserMessageArgs));
  }

  private AbstractPlatformException(
      String i18nMessageCode,
      String defaultUserMessage,
      Throwable cause,
      Object[] defaultUserMessageArgs) {
    super(defaultUserMessage, cause);
    this.i18nMessageCode = i18nMessageCode;
    this.defaultUserMessage = defaultUserMessage;
    this.defaultUserMessageArgs =
        defaultUserMessageArgs.length == 0 ? NO_ARGS : defaultUserMessageArgs.clone();
  }

  private static Throwable findThrowableCause(Object[] defaultUserMessageArgs) {
    if (defaultUserMessageArgs == null) {
      return null;
    }
    for (Object defaultUserMessageArg : defaultUserMessageArgs) {
      if (defaultUserMessageArg instanceof Throwable) {
        return (Throwable) defaultUserMessageArg;
      }
    }
    return null;
  }

  private static Object[] filterThrowableCause(Object[] defaultUserMessageArgs) {
    if (defaultUserMessageArgs == null) {
      return NO_ARGS;
    }
    List<Object> filteredDefaultUserMessageArgs = new ArrayList<>(defaultUserMessageArgs.length);
    for (Object defaultUserMessageArg : defaultUserMessageArgs) {
      if (!(defaultUserMessageArg instanceof Throwable)) {
        filteredDefaultUserMessageArgs.add(defaultUserMessageArg);
      }
    }
    return filteredDefaultUserMessageArgs.toArray();
  }

  /** Returns the i18n message code used for translation lookup. */
  public final String geti18nMessageCode() {
    return this.i18nMessageCode;
  }

  public final String getDefaultUserMessage() {
    return this.defaultUserMessage;
  }

  /** Returns interpolation arguments associated with the default user message. */
  public final Object[] getDefaultUserMessageArgs() {
    return this.defaultUserMessageArgs.length == 0 ? NO_ARGS : this.defaultUserMessageArgs.clone();
  }
}
