package com.machingclee.rabbitmq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import com.machingclee.rabbitmq.controller.MessageController;

@Configuration
public class RoutingConfig {
    @Bean
    public RouterFunction<ServerResponse> studentRouter(MessageController msgController) {
        return RouterFunctions.route()
                .GET("/ttl/msg/{message}", RequestPredicates.accept(MediaType.ALL), msgController::sendMessage)
                .build();
    }
}