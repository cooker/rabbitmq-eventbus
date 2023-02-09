package com.github.grant.rabbitmq.springstream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @Author: kqyu
 * @Date: 2023/2/9 16:14
 * @Description:
 */
@Service
public class MqSer {
    @Resource
    private MQConfig mqConfig;

    public void send() {
        mqConfig.testOut().send(MessageBuilder.createMessage("sa", new MessageHeaders(new HashMap<>())));
    }

    @StreamListener("test_dlq_in")
    @SendTo("qx_close_out")
    public String dlqRouter(String close) {
        System.out.println("小佛close");
        return close;
    }
}
