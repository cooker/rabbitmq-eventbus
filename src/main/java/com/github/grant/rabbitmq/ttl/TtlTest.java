package com.github.grant.rabbitmq.ttl;

import com.github.grant.rabbitmq.BaseTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import lombok.Cleanup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: kqyu
 * @Date: 2023/2/7 12:14
 * @Description:
 */
public class TtlTest extends BaseTest {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = factory();
        @Cleanup Connection conn = factory.newConnection();
        @Cleanup Channel channel = conn.createChannel();
        channel.exchangeDeclare("ttl", BuiltinExchangeType.TOPIC);
        Map<String, Object> params = new HashMap<>();
        params.put("x-message-ttl", 50000);

        channel.queueDeclare("ttl.1", true, false, true, params);
        channel.queueBind("ttl.1", "ttl", "#");

        for (int i = 0; i < 10; i++) {
            channel.basicPublish("ttl", "ttl.1", MessageProperties.TEXT_PLAIN, "sa".getBytes(StandardCharsets.UTF_8));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
