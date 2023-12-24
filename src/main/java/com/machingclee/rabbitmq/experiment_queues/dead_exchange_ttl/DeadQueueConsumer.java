package com.machingclee.rabbitmq.experiment_queues.dead_exchange_from_rejected;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.machingclee.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.DeliverCallback;

public class DeadQueueConsumer {
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
            channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        } catch (Exception err) {
            System.out.println(err);
        }
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, DEAD_ROUTING_KEY);

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("[DeadQueue Consumer Digested]" + new String(message.getBody()));
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("[Message Cancelled]");
        };

        channel.basicConsume(DEAD_QUEUE, false, deliverCallback, cancelCallback);
    }
}
