package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单超时取消任务，每分钟执行一次，检查15分钟内还未支付的单子，更改状态为取消
     */
    //@Scheduled(cron = "1/5 * * * * ?")
    @Scheduled(cron = "0 0/1 * * * ?")
    public void ordertimeouttask(){
        // 计算15分钟前的时间点
        log.info("订单超时取消任务开始执行");
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        // 查询状态为待付款且下单时间早于15分钟前的订单
        List<Orders> orderList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

        if (orderList != null && !orderList.isEmpty()) {
            log.info("发现 {} 笔超时未支付订单，开始取消", orderList.size());
            for (Orders order : orderList) {
                Orders orders = new Orders();
                orders.setId(order.getId());
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("超时未支付，系统自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }


    /**
     * 凌晨一点执行，检查昨天派送中的订单，更改状态为取消
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void deliveryTask(){
        log.info("订单派送中取消任务开始执行");
        // 今天凌晨的时间点
        LocalDateTime time = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        // 查询状态为派送中且下单时间早于今天凌晨的订单（即昨天及更早的派送中订单）
        List<Orders> orderList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        if (orderList != null && !orderList.isEmpty()) {
            log.info("发现 {} 笔昨日派送中订单，开始取消", orderList.size());
            for (Orders order : orderList) {
                Orders orders = new Orders();
                orders.setId(order.getId());
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

}
