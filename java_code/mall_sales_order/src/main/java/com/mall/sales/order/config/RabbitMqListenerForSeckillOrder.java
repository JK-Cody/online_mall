package com.mall.sales.order.config;

import com.mall.common.to.SeckillSkuCreateOrderTO;
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
 * 监视秒杀商品的订单队列的消息监测器
 */
@Slf4j
@Service
@RabbitListener(queues = "order.seckill.order.queue")     //目标消息队列
public class RabbitMqListenerForSeckillOrder {

    @Autowired
    OrderService orderService;

    /**
     * 创建秒杀商品的订单
     */
    @RabbitHandler
    public void handleOrderLockedRelease(SeckillSkuCreateOrderTO createOrder, Channel channel, Message message) throws IOException {

        log.info("*************创建秒杀商品的订单***************");
        try {
            orderService.saveSeckillSkuCreateOrder(createOrder);
            // 手动确认消息消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); //接收
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);  //拒绝接收
        }
    }
}
