package com.github.grant.rabbitmq.dlx;

import com.github.grant.rabbitmq.BaseTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import lombok.Cleanup;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: kqyu
 * @Date: 2023/2/7 17:18
 * @Description:
 */
public class DlxTest extends BaseTest {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = factory();
        @Cleanup Connection conn = factory.newConnection();
        @Cleanup Channel channel = conn.createChannel();
        channel.exchangeDeclare("qx.close", BuiltinExchangeType.DIRECT);
        channel.queueDeclare("qx.close", true, false, false, null);
        channel.queueBind("qx.close", "qx.close", "#");

        channel.exchangeDeclare("dlx", BuiltinExchangeType.TOPIC);
        Map<String, Object> params = new HashMap<>();
        params.put("x-message-ttl", 50000);
        params.put("x-dead-letter-exchange", "qx.close");
        params.put("x-dead-letter-routing-key", "#");
        channel.queueDeclare("dlx.1", true, false, true, params);
        channel.queueBind("dlx.1", "dlx", "#");
//        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
//        builder.expiration("15000");
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("dlx", "dlx.1", MessageProperties.TEXT_PLAIN, "sa".getBytes(StandardCharsets.UTF_8));
            TimeUnit.SECONDS.sleep(1);
        }

        //拒绝直接进入死性队列
        channel.basicConsume("dlx.1", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("拒绝");
//                getChannel().basicReject(envelope.getDeliveryTag(), false);
                throw new AmqpRejectAndDontRequeueException("拒绝");
            }
        });

        TimeUnit.SECONDS.sleep(10);
    }
}
