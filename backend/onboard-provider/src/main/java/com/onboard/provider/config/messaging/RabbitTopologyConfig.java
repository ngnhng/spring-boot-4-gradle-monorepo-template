package com.onboard.provider.config.messaging;

import com.onboard.infrastructure.core.config.PlatformProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declares RabbitMQ exchanges, queues, and bindings from platform properties. */
@Configuration
public class RabbitTopologyConfig {

  /**
   * Builds AMQP declarables for configured exchanges, queues, and bindings.
   *
   * @param platformProperties platform configuration properties
   * @return all AMQP declarables to register at startup
   */
  @Bean
  public Declarables rabbitDeclarables(PlatformProperties platformProperties) {
    PlatformProperties.MessagingProperties messagingProperties = platformProperties.getMessaging();
    if (messagingProperties == null || messagingProperties.getRabbit() == null) {
      return new Declarables();
    }

    PlatformProperties.MessagingProperties.RabbitProperties rabbitProperties =
        messagingProperties.getRabbit();
    List<Declarable> declarables = new ArrayList<>();
    Map<String, Exchange> exchanges = new HashMap<>();
    Map<String, Queue> queues = new HashMap<>();

    for (PlatformProperties.MessagingProperties.RabbitProperties.Exchange exchangeProperties :
        rabbitProperties.getExchanges()) {
      Exchange exchange = toExchange(exchangeProperties);
      exchanges.put(exchangeProperties.getName(), exchange);
      declarables.add(exchange);
    }

    for (PlatformProperties.MessagingProperties.RabbitProperties.Queue queueProperties :
        rabbitProperties.getQueues()) {
      Queue queue = toQueue(queueProperties);
      queues.put(queueProperties.getName(), queue);
      declarables.add(queue);
    }

    for (PlatformProperties.MessagingProperties.RabbitProperties.Binding bindingProperties :
        rabbitProperties.getBindings()) {
      Queue queue = queues.get(bindingProperties.getQueue());
      Exchange exchange = exchanges.get(bindingProperties.getExchange());
      if (queue == null || exchange == null) {
        throw new IllegalStateException("Invalid rabbit binding definition: " + bindingProperties);
      }
      declarables.add(toBinding(queue, exchange, bindingProperties.getRoutingKey()));
    }

    return new Declarables(declarables);
  }

  private static Exchange toExchange(
      PlatformProperties.MessagingProperties.RabbitProperties.Exchange exchangeProperties) {
    String type = exchangeProperties.getType();
    if ("direct".equalsIgnoreCase(type)) {
      return new DirectExchange(
          exchangeProperties.getName(),
          exchangeProperties.isDurable(),
          exchangeProperties.isAutoDelete());
    }
    if ("fanout".equalsIgnoreCase(type)) {
      return new FanoutExchange(
          exchangeProperties.getName(),
          exchangeProperties.isDurable(),
          exchangeProperties.isAutoDelete());
    }
    return new TopicExchange(
        exchangeProperties.getName(),
        exchangeProperties.isDurable(),
        exchangeProperties.isAutoDelete());
  }

  private static Binding toBinding(Queue queue, Exchange exchange, String routingKey) {
    if (exchange instanceof TopicExchange topicExchange) {
      return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
    if (exchange instanceof DirectExchange directExchange) {
      return BindingBuilder.bind(queue).to(directExchange).with(routingKey);
    }
    if (exchange instanceof FanoutExchange fanoutExchange) {
      return BindingBuilder.bind(queue).to(fanoutExchange);
    }
    throw new IllegalStateException("Unsupported exchange type for binding: " + exchange.getName());
  }

  private static Queue toQueue(
      PlatformProperties.MessagingProperties.RabbitProperties.Queue queueProperties) {
    QueueBuilder builder =
        queueProperties.isDurable()
            ? QueueBuilder.durable(queueProperties.getName())
            : QueueBuilder.nonDurable(queueProperties.getName());
    if (queueProperties.isQuorum()) {
      builder = builder.quorum();
    }
    if (queueProperties.getDeadLetterExchange() != null
        && !queueProperties.getDeadLetterExchange().isBlank()) {
      builder = builder.deadLetterExchange(queueProperties.getDeadLetterExchange());
    }
    if (queueProperties.getDeadLetterRoutingKey() != null
        && !queueProperties.getDeadLetterRoutingKey().isBlank()) {
      builder = builder.deadLetterRoutingKey(queueProperties.getDeadLetterRoutingKey());
    }
    return builder.build();
  }
}
