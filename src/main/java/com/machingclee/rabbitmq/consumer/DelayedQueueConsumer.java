package com.machingclee.rabbitmq.consumer;

import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.machingclee.rabbitmq.config.DelayedQueueConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DelayedQueueConsumer {
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayedQueue(Message message) {
        String msg = new String(message.getBody());
        log.info("[Delayed Consumer] {}, {}", new Date().toString(), msg);
    }
}
