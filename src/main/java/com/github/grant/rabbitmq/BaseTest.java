package com.github.grant.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author: kqyu
 * @Date: 2023/2/7 10:37
 * @Description:
 */
public class BaseTest {
    /**
     * @return
     */
    public static ConnectionFactory factory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(ConnectionFactory.DEFAULT_AMQP_PORT);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        return factory;
    }

}
