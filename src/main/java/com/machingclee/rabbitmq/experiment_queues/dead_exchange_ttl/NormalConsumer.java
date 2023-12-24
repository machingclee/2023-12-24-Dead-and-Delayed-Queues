package com.machingclee.rabbitmq.experiment_queues.dead_exchange_ttl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.machingclee.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.DeliverCallback;

public class NormalConsumer {
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    public static final String DEAD_EXCHANGE = "dead_exchange";
    public static final String NORMAL_QUEUE = "normal_queue";
    public static final String DEAD_QUEUE = "dead_queue";

    public static final String NORMAL_ROUTING_KEY = "normal_route";
    public static final String DEAD_ROUTING_KEY = "dead_route";

    public static void main(String[] args) throws IOException, TimeoutException {
        var channel = RabbitMQUtil.getChannel();

        // create exchanges
        try {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
            channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        } catch (Exception err) {
            System.out.println(err);
        }

        // special config for normal queue to communicate with dead_exchange
        // and dead_queue
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);

        // create queues
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);

        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, NORMAL_ROUTING_KEY);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, DEAD_ROUTING_KEY);

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody());
            System.out.println("[DeadQueue Consumer Digested]" + msg);
            if (msg.equals("info " + 5)) {
                System.out.println("reject message: " + msg);
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            } else {
                System.out.println("message acked: " + msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("[Message Cancelled]");
        };

        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, cancelCallback);
    }
}
