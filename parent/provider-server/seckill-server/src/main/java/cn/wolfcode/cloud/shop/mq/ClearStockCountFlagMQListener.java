package cn.wolfcode.cloud.shop.mq;

import cn.wolfcode.cloud.shop.web.controller.OrderInfoController;
import cn.wolfcode.shop.consts.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 清除本地售完标记消息监听器
 */
@RocketMQMessageListener(
        topic = MQConstants.SECKILL_ORDER_TOPIC,
        selectorExpression = MQConstants.CLEAR_STOCK_COUNT_FLAG_TAG,
        consumerGroup = MQConstants.CLEAR_STOCK_COUNT_FLAG_CONSUMER_GROUP,
        messageModel = MessageModel.BROADCASTING
)
@Component
@Slf4j
public class ClearStockCountFlagMQListener implements RocketMQListener<Long> {

    public void onMessage(Long id) {
        log.info("收到清除本地售完标记消息:{}", id);
        // 直接将标记的map中的true修改为false
        OrderInfoController.STOCK_COUNT_MAP.put(id, false);
    }
}
