package com.onboard.infrastructure.core.idempotency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/** Marks a method as idempotent and caches successful responses by idempotency key. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

  /** Optional SpEL expression to resolve the idempotency key from method arguments. */
  String key() default "";

  /** Optional cache namespace. If empty, a default namespace is derived from method signature. */
  String namespace() default "";

  /** Expiration amount for cached idempotent responses. */
  long expire() default 60;

  /** Time unit for {@link #expire()}. */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

  /**
   * Whether to fail when idempotency key is not provided.
   *
   * <p>If false, the annotated method proceeds normally without idempotent cache behavior.
   */
  boolean required() default false;
}
