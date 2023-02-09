package com.github.grant.rabbitmq.springstream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: kqyu
 * @Date: 2023/2/9 14:25
 * @Description:
 */
public interface MQConfig {
    @Output("test_out")
    MessageChannel testOut();

    @Input("test_dlq_in")
    SubscribableChannel testDlqIn();

    @Output("qx_close_out")
    SubscribableChannel qxCloseOut();
}
