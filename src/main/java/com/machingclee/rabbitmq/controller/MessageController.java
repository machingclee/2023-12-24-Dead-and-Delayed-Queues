package com.machingclee.rabbitmq.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.machingclee.rabbitmq.config.TTLQueueConfig;

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

        log.info("Current Time: {}, sent an message to 10s-ttl queues: {}",
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
}
