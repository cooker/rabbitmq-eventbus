package com.github.grant.rabbitmq.queue;

import com.github.grant.rabbitmq.BaseTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: kqyu
 * @Date: 2023/2/7 10:51
 * @Description:
 */
public class QueueTest extends BaseTest {

    public static final String exchange = "qx";
    public static final String routeKey = "queue.1";
    public static final String routeKey2 = "queue.2";
    public static final String routeKey3 = "queue.3";

    public static final String MSG = "{}";

    /**
     * 交换机 帮定 队列
     * @param args
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = factory();
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true);

        channel.queueDeclare(routeKey, true, false, false, new HashMap<>());
        channel.queueBind(routeKey, exchange, routeKey);
        channel.basicQos(10);//预取数
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.contentType("application/json");
        builder.contentEncoding("utf-8");
        builder.deliveryMode(2);
        //发送
        channel.basicPublish(exchange, routeKey, builder.build(), MSG.getBytes(StandardCharsets.UTF_8));

        channel.queueDeclare(routeKey2, true, false, false, new HashMap<>());
        channel.queueBind(routeKey2, exchange, routeKey2);
        //发送
        channel.basicPublish(exchange, routeKey2, builder.build(), MSG.getBytes(StandardCharsets.UTF_8));
        CountDownLatch downLatch = new CountDownLatch(1);
        channel.basicConsume(routeKey2, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                Map<String, Object> params = new HashMap<>();
                params.put("x-message-ttl", 3000);
                params.put("x-dead-letter-exchange", "qx.close");
                params.put("x-dead-letter-routing-key", "#");

                channel.exchangeDeclare("qx.close", BuiltinExchangeType.DIRECT);
                channel.queueDeclare("qx.close", true, false, false, null);
                channel.queueBind("qx.close", "qx.close", "#");
                channel.queueDeclare(routeKey3, true, false, false, params);
                channel.queueBind(routeKey3, exchange, routeKey3);
                AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
                builder.contentType("application/json");
                builder.contentEncoding("utf-8");

                channel.basicPublish(exchange, routeKey3, builder.build(), MSG.getBytes(StandardCharsets.UTF_8));
                System.out.println("消费：" + new String(body));
                //确认
                // channel.basicAck(deliveryTag, false);
                channel.basicReject(deliveryTag, false);
                downLatch.countDown();
            }
        });

        downLatch.await(3, TimeUnit.SECONDS);
        Thread.sleep(1000);
        channel.close();
        conn.close();
    }
}
