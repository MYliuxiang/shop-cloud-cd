package cn.wolfcode.cloud.shop.mq;

import cn.wolfcode.cloud.shop.mq.msg.CreateOrderMsg;
import cn.wolfcode.cloud.shop.mq.msg.SeckillOrderDelayMsg;
import cn.wolfcode.cloud.shop.service.IOrderInfoService;
import cn.wolfcode.shop.consts.MQConstants;
import cn.wolfcode.shop.mq.msg.SeckillSuccessMsg;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * 创建秒杀订单接口消息监听器
 */
@RocketMQMessageListener(
        topic = MQConstants.SECKILL_ORDER_TOPIC,
        selectorExpression = MQConstants.CREATE_ORDER_TAG,
        consumerGroup = MQConstants.CREATE_ORDER_CONSUMER_GROUP
)
@Component
@Slf4j
public class CreateSeckillOrderMQListener implements RocketMQListener<CreateOrderMsg> {

    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void onMessage(CreateOrderMsg msg) {
        log.info("接收到创建秒杀订单消息:{}", JSON.toJSONString(msg));
        // 调用创建秒杀订单的接口
        try {
            String orderNo = orderInfoService.doSeckill(msg.getSeckillId(), msg.getUserId());
            // 如果没有出现异常, 表示订单创建成功, 发送成功消息, 提示用户
            rocketMQTemplate.syncSend(MQConstants.SECKILL_SUCCESS_DEST,
                    new SeckillSuccessMsg(msg.getUuid(), orderNo));

            // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
            // 如果订单创建成功, 发送一个延迟消息, 用于30分钟后监听该订单是否已支付
            GenericMessage<?> message =
                    new GenericMessage<>(new SeckillOrderDelayMsg(orderNo, msg.getSeckillId()));
            rocketMQTemplate.syncSend(MQConstants.SECKILL_SUCCESS_DELAY_DEST,
                    message, 5000, 16);
        } catch (Exception e) {
            // 如果调用创建订单出现异常, 此时表示订单创建失败
            e.printStackTrace();
            // 1. 发送订单创建失败的消息, 通知客户端
            rocketMQTemplate.syncSend(MQConstants.SECKILL_FAILED_DEST, msg.getUuid());
            // 2. 回补 Redis 库存
            // 3. 清楚本地售完标记
            orderInfoService.handlerFailedSeckill(msg.getSeckillId());
        }
    }
}
