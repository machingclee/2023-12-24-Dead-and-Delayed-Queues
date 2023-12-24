package com.machingclee.rabbitmq.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeadLetterQueueConsumer {
    @RabbitListener(queues = "DEAD_LETTER_Q")
    public void receiveDeadMsg(Message message, Channel channel) {

        String msg = new String(message.getBody());
        log.info("Current time: {}, dead queue message: {}",
                new java.util.Date().toString(),
                msg);
    }
}
