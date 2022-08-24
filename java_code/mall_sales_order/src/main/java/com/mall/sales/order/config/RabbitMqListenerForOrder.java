package com.mall.sales.order.config;

import com.mall.sales.order.entity.OrderEntity;
import com.mall.sales.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 监视订单队列的消息监测器
 */
@Slf4j
@Service
@RabbitListener(queues = "order.release.order.queue")     //目标消息队列
public class RabbitMqListenerForOrder{

    @Autowired
    OrderService orderService;

    /**
     * 每个订单都经过监测环节，检测订单的状态
     * 超过时间未支付的订单都设置已取消，并恢复锁定的库存
     */
    @RabbitHandler
    public void handleOrderLockedRelease(OrderEntity orderEntity,Channel channel,Message message) throws IOException {

        log.info("*************检测订单的状态***************");
        try {
            orderService.confirmOrderClose(orderEntity);
            // 手动确认消息消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); //接收
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);  //拒绝接收
        }
    }

}

