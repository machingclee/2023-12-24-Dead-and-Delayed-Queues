package com.machingclee.rabbitmq.dead_exchange_from_rejected;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.machingclee.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;

public class Producer {
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    public static final String NORMAL_ROUTING_KEY = "normal_route";

    public static void main(String[] args) throws IOException, TimeoutException {
        var channel = RabbitMQUtil.getChannel();
        try {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        } catch (Exception err) {
            System.out.println(err);
        }

        BasicProperties properites = new BasicProperties().builder()
                .expiration("10000")
                .build();

        for (int i = 0; i < 10; i++) {
            var message = "info " + (i + 1);
            channel.basicPublish(NORMAL_EXCHANGE, NORMAL_ROUTING_KEY, properites, message.getBytes());
            System.out.println("info " + (i + 1) + " was sent");
        }
    }
}
