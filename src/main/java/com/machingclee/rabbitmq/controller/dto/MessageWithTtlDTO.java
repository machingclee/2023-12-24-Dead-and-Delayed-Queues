package com.machingclee.rabbitmq.controller.dto;

import lombok.Data;

@Data
public class MessageWithTtlDTO {
    private String message;
    private String ttl;
}
