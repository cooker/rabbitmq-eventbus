package com.github.grant.rabbitmq.fanout;

import com.github.grant.rabbitmq.BaseTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Cleanup;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @Author: kqyu
 * @Date: 2023/2/7 17:47
 * @Description:
 */
public class FanoutTest extends BaseTest {

    public static final String exchange = "fan";
    public static final String routeKey = "fan.1";
    public static final String routeKey2 = "fan.2";
    public static final String routeKey3 = "fan.3";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = factory();
        @Cleanup Connection conn = factory.newConnection();
        @Cleanup Channel channel = conn.createChannel();
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);

        channel.queueDeclare(routeKey, true, false, false, new HashMap<>());
        channel.queueDeclare(routeKey2, true, false, false, new HashMap<>());
        channel.queueDeclare(routeKey3, true, false, false, new HashMap<>());

        channel.queueBind(routeKey, exchange, routeKey);
        channel.queueBind(routeKey2, exchange, routeKey);
        channel.queueBind(routeKey3, exchange, routeKey);

        channel.basicQos(10);//预取数
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.contentType("application/json");
        builder.contentEncoding("utf-8");
        builder.deliveryMode(2);
        //发送
        channel.basicPublish(exchange, routeKey2, builder.build(), "MSG".getBytes(StandardCharsets.UTF_8));
    }
}
