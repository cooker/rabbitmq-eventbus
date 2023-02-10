package com.github.grant.rabbitmq.springstream;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: kqyu
 * @Date: 2023/2/9 13:59
 * @Description:
 */
@EnableBinding(MQConfig.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * rabbitTemplate 才能使用
     * @return
     */
    @Bean
    public Queue timeOutQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 50000);
        args.put("x-dead-letter-exchange", "qx.close");
        args.put("x-dead-letter-routing-key", "#");
        return new Queue("timeOut", true, false, false, args);
    }
    @Bean
    public TopicExchange timeOutExchange() {
        return new TopicExchange("timeOut");
    }

    @Bean
    public Binding timeOutBind() {
        return BindingBuilder.bind(new Queue("timeOut")).to(timeOutExchange()).with("#");
    }

}
