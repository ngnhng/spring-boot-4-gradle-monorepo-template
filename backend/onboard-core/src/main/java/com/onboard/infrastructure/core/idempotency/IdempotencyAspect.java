package com.onboard.infrastructure.core.idempotency;

import com.onboard.infrastructure.core.config.PlatformProperties;
import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

/** Aspect-based idempotency mode for methods annotated with {@link Idempotent}. */
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

  private final IdempotencyService idempotencyService;
  private final PlatformProperties platformProperties;
  private final ObjectMapper objectMapper;

  private final ExpressionParser spelParser = new SpelExpressionParser();
  private final ParameterNameDiscoverer parameterNameDiscoverer =
      new DefaultParameterNameDiscoverer();

  /**
   * Wraps the intercepted method call with idempotency key resolution and cached response reuse.
   *
   * @param joinPoint intercepted method invocation
   * @param idempotent annotation metadata
   * @return cached or newly computed response
   * @throws Throwable propagated invocation failure
   */
  @Around("@annotation(idempotent)")
  public Object enforceIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    String key = resolveKey(joinPoint, method, idempotent);
    if (!StringUtils.hasText(key)) {
      if (idempotent.required()) {
        throw new IllegalArgumentException("Missing idempotency key");
      }
      return joinPoint.proceed();
    }

    String namespace = resolveNamespace(method, idempotent);
    Duration ttl = Duration.ofMillis(idempotent.timeUnit().toMillis(idempotent.expire()));
    JavaType returnType =
        objectMapper.getTypeFactory().constructType(method.getGenericReturnType());

    try {
      return idempotencyService.execute(
          namespace,
          key,
          ttl,
          returnType,
          () -> {
            try {
              return joinPoint.proceed();
            } catch (Throwable ex) {
              throw new InvocationFailureException(ex);
            }
          });
    } catch (InvocationFailureException ex) {
      throw ex.getCause();
    }
  }

  private String resolveKey(ProceedingJoinPoint joinPoint, Method method, Idempotent idempotent) {
    if (StringUtils.hasText(idempotent.key())) {
      return resolveFromSpel(joinPoint, method, idempotent.key());
    }

    String headerName = platformProperties.getIdempotencyKeyHeaderName();
    if (!StringUtils.hasText(headerName)) {
      return null;
    }

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return null;
    }

    String headerValue = attributes.getRequest().getHeader(headerName);
    return StringUtils.hasText(headerValue) ? headerValue : null;
  }

  private String resolveFromSpel(ProceedingJoinPoint joinPoint, Method method, String expression) {
    StandardEvaluationContext context = new StandardEvaluationContext();
    Object[] args = joinPoint.getArgs();
    context.setVariable("args", args);

    String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
    if (parameterNames != null) {
      for (int i = 0; i < parameterNames.length && i < args.length; i++) {
        context.setVariable(parameterNames[i], args[i]);
      }
    }

    Object value = spelParser.parseExpression(expression).getValue(context);
    if (value == null) {
      return null;
    }

    String key = value.toString();
    return StringUtils.hasText(key) ? key : null;
  }

  private static String resolveNamespace(Method method, Idempotent idempotent) {
    if (StringUtils.hasText(idempotent.namespace())) {
      return idempotent.namespace();
    }
    return method.getDeclaringClass().getSimpleName() + "." + method.getName();
  }

  private static final class InvocationFailureException extends RuntimeException {
    private InvocationFailureException(Throwable cause) {
      super(cause);
    }
  }
}
