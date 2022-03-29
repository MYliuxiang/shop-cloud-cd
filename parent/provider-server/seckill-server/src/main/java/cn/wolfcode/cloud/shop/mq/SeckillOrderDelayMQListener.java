package cn.wolfcode.cloud.shop.mq;

import cn.wolfcode.cloud.shop.mq.msg.SeckillOrderDelayMsg;
import cn.wolfcode.cloud.shop.service.IOrderInfoService;
import cn.wolfcode.shop.consts.MQConstants;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单延迟消息监听器: 用于监听超过 30 分钟还没有支付的订单
 */
@RocketMQMessageListener(
        topic = MQConstants.SECKILL_ORDER_TOPIC,
        selectorExpression = MQConstants.SECKILL_SUCCESS_DELAY_TAG,
        consumerGroup = MQConstants.SECKILL_SUCCESS_DELAY_CONSUMER_GROUP
)
@Component
@Slf4j
public class SeckillOrderDelayMQListener implements RocketMQListener<SeckillOrderDelayMsg> {

    @Autowired
    private IOrderInfoService orderInfoService;

    public void onMessage(SeckillOrderDelayMsg msg) {
        log.info("收到订单延迟消息:{}", JSON.toJSONString(msg));
        // 检查订单是否已超时
        orderInfoService.checkOrderTimeOut(msg.getOrderNo(), msg.getSeckillId());
    }
}
