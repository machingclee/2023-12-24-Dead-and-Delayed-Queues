package com.machingclee.rabbitmq.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.machingclee.rabbitmq.config.DelayedQueueConfig;
import com.machingclee.rabbitmq.config.TTLQueueConfig;
import com.machingclee.rabbitmq.controller.dto.MessageWithTtlDTO;
import com.rabbitmq.client.AMQP.BasicProperties;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageController {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    MessageController(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public ServerResponse sendMessage(ServerRequest req) {
        var message = req.pathVariable("message");

        rabbitTemplate.convertAndSend(
                TTLQueueConfig.NORMAL_EXCHANGE,
                TTLQueueConfig.NORMAL_Q_A_ROUTING_KEY,
                "[Send to 10s ttl queue] " + message);

        log.info("Current Time: {}, sent an message t10s-ttl queues: {}",
                new Date().toString(),
                message);
        rabbitTemplate.convertAndSend(
                TTLQueueConfig.NORMAL_EXCHANGE,
                TTLQueueConfig.NORMAL_Q_B_ROUTING_KEY,
                "[Send to 40s ttl queue] " + message);
        log.info("Current Time: {}, sent an message to 40s-ttl queues: {}",
                new Date().toString(),
                message);

        return ServerResponse.ok().body(Map.of("result", message));
    }

    public ServerResponse sendMessageWithTTL(ServerRequest req) throws IOException, ServletException {
        MessageWithTtlDTO body = req.body(MessageWithTtlDTO.class);

        var message = body.getMessage();
        var ttl = body.getTtl();

        rabbitTemplate.convertAndSend(
                TTLQueueConfig.NORMAL_EXCHANGE,
                TTLQueueConfig.NORMAL_Q_C_ROUTING_KEY,
                "[custom ttl message: ]" + message,
                msg -> {
                    msg.getMessageProperties().setExpiration(ttl);
                    return msg;
                });
        log.info("Custom ttl message was sent: " + message);
        return null;
    }

    public ServerResponse delayedMessage(ServerRequest req) {
        var ttl = Integer.parseInt(req.pathVariable("ttl"));
        var message = req.pathVariable("msg");

        log.info("[message delayed for {}s]: {}", ttl, message);
        rabbitTemplate.convertAndSend(
                DelayedQueueConfig.DELAYED_QUEUE_EXCHANGE,
                DelayedQueueConfig.DELAYED_QUEUE_ROUTING_KEY,
                "[message with ttl: " + ttl + "] " + message,
                msg -> {
                    msg.getMessageProperties().setDelay(ttl);
                    return msg;
                });

        return ServerResponse.ok().body(Map.of("result", message));
    }
}
