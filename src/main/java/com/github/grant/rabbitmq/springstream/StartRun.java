package com.github.grant.rabbitmq.springstream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: kqyu
 * @Date: 2023/2/9 14:36
 * @Description:
 */
@Slf4j
@Component
public class StartRun implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private MqSer mqSer;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            log.info("消息发送");
            new Thread(()->{
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println("hdjkashdiuqwhuioheuioqwhioue");
                    mqSer.send();
                } catch (Exception e) {
                    log.error("yic", e);
                }
            }).start();
        }
        //        rabbitTemplate.convertAndSend("timeOut", "#", "sa");
    }
}