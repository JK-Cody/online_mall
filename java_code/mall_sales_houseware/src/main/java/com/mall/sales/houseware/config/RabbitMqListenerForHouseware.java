package com.mall.sales.houseware.config;

import com.mall.common.to.OrderTO;
import com.mall.sales.houseware.service.WareSkuService;
import com.mall.sales.houseware.to.OrderDetailLockedTO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 监视库存队列的消息监测器
 */
@Slf4j
@Service
@RabbitListener(queues = "stock.release.stock.queue")   //目标消息队列
public class RabbitMqListenerForHouseware {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 监测器检测仓库工作单状态进行解锁
     */
    @RabbitHandler
    public void handleStockLockedRelease(OrderDetailLockedTO orderDetailLockedTO, Message message, Channel channel) throws IOException {

        log.info("*************收到库存解锁的消息***************");
        try {
            wareSkuService.getSkuStockUnlock(orderDetailLockedTO);
            // 手动确认消息消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); //接收
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);  //拒绝接收
        }
    }

    /**
     * 监测器检测订单关闭时,进行仓库工作单解锁
     */
    @RabbitHandler
    public void handleOrderCloseRelease(OrderTO orderTO, Message message, Channel channel) throws IOException {

        log.info("*************订单关闭准备解锁库存***************");
        try{
            wareSkuService.getSkuStockUnlock(orderTO);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}

