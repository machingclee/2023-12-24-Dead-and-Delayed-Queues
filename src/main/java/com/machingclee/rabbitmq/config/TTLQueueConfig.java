package com.machingclee.rabbitmq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TTLQueueConfig {
    public static final String NORMAL_EXCHANGE = "NORMAL_EXCHANGE";
    public static final String DEAD_LETTER_EXCANGE = "DEAD_LETTER_EXCANGE";

    public static final String NORMAL_Q_A = "NORMAL_Q_A";
    public static final String NORMAL_Q_A_ROUTING_KEY = "NORMAL_Q_A_ROUTING_KEY";
    public static final String NORMAL_Q_B = "NORMAL_Q_B";
    public static final String NORMAL_Q_B_ROUTING_KEY = "NORMAL_Q_B_ROUTING_KEY";
    public static final String DEAD_LETTER_Q = "DEAD_LETTER_Q";
    public static final String DEAD_LETTER_Q_ROUTING_KEY = "DEAD_LETTER_Q_ROUTING_KEY";

    public static final String NORMAL_Q_C = "NORMAL_Q_C";
    public static final String NORMAL_Q_C_ROUTING_KEY = "NORMAL_Q_C_ROUTING_KEY";

    @Bean
    public DirectExchange xExchange() {
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_LETTER_EXCANGE);
    }

    @Bean
    public Queue queueA() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_LETTER_EXCANGE);
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_Q_ROUTING_KEY);
        arguments.put("x-message-ttl", 10000);

        return QueueBuilder.durable(NORMAL_Q_A).withArguments(arguments).build();
    }

    @Bean
    public Queue queueB() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_LETTER_EXCANGE);
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_Q_ROUTING_KEY);
        arguments.put("x-message-ttl", 40000);

        return QueueBuilder.durable(NORMAL_Q_B).withArguments(arguments).build();
    }

    @Bean
    public Queue queueC() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_LETTER_EXCANGE);
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_Q_ROUTING_KEY);

        return QueueBuilder.durable(NORMAL_Q_C).withArguments(arguments).build();
    }

    @Bean
    public Queue queueD() {
        return QueueBuilder.durable(DEAD_LETTER_Q).build();
    }

    @Bean
    public Binding bindAToNormalExchange(@Qualifier("queueA") Queue queueA,
            @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with(NORMAL_Q_A_ROUTING_KEY);
    }

    @Bean
    public Binding bindBToNormalExchange(
            @Qualifier("queueB") Queue queueB,
            @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueB).to(xExchange).with(NORMAL_Q_B_ROUTING_KEY);
    }

    @Bean
    public Binding bindCToNormalExchange(
            @Qualifier("queueC") Queue queueC,
            @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueC).to(xExchange).with(NORMAL_Q_C_ROUTING_KEY);
    }

    @Bean
    public Binding bindDeadQueueToDeadExchange(
            @Qualifier("queueD") Queue queueD,
            @Qualifier("deadExchange") DirectExchange deadExchange) {
        return BindingBuilder.bind(queueD).to(deadExchange).with(DEAD_LETTER_Q_ROUTING_KEY);
    }
}
