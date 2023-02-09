package com.github.grant.rabbitmq.recon;

import com.github.grant.rabbitmq.BaseTest;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.aop.MethodBeforeAdvice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

/**
 * @Author: kqyu
 * @Date: 2023/2/7 11:57
 * @Description:
 */
public class ReconTest extends BaseTest {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //消费者无法重连
//        ConnectionFactory factory = factory();
//        Connection conn = factory.newConnection();
//        Channel channel = conn.createChannel(2);
//        channel.basicQos(10);
//        channel.basicConsume("queue.1", true, new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                System.out.println("消费：" + new String(body));
//            }
//        });
//
//        Thread.sleep(30000L);
//        channel.close();
//        conn.close();

        //可重连
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("127.0.0.1", 5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setConnectionLimit(2);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        MessageListenerAdapter listener = new MessageListenerAdapter(new Object() {
            public void handleMessage(String json, Channel channel, Message msg) throws IOException {
                System.out.println("消费：" + json);
                channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
            }
        }) {
            protected Object[] buildListenerArguments(Object extractedMessage, Channel channel, Message message) {
                return new Object[]{extractedMessage, channel, message};
            }
        };
        listener.setMessageConverter(new SimpleMessageConverter(){
            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                return new String(message.getBody());
            }
        });
        container.setAdviceChain(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] objects, Object o) throws Throwable {
                System.out.println("执行方法: " + method.getName());
            }
        });
        container.setMessageListener(listener);
        container.setQueueNames("queue.1","queue.2");
        container.setBatchSize(10);
        container.setPrefetchCount(10);
        container.start();

        Thread.sleep(30000L);
    }
}
