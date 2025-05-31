package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? *")

    private void processTimeOrder(){
        log.info("处理超时订单：{}", LocalDateTime.now());

       List<Orders> ordersList =  orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));

       if(ordersList.size()>0 && ordersList != null){
           for(Orders orders : ordersList){
               orders.setStatus(Orders.CANCELLED);
               orders.setCancelReason("订单超时，自动取消");
               orders.setCancelTime(LocalDateTime.now());
               orderMapper.update(orders);
           }
       }
    }

    /**
     * 定时处理派送中订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void processDeliveryOrder(){
        log.info("定时处理派送中订单：{}", LocalDateTime.now());

        List<Orders> ordersList =  orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));

        if(ordersList.size()>0 && ordersList != null){
            for(Orders orders : ordersList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
