package cn.wolfcode.cloud.shop.mq;

import cn.wolfcode.cloud.shop.ws.WebSocketServer;
import cn.wolfcode.shop.common.Result;
import cn.wolfcode.shop.consts.MQConstants;
import cn.wolfcode.shop.mq.msg.SeckillSuccessMsg;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@RocketMQMessageListener(
        topic = MQConstants.SECKILL_ORDER_TOPIC,
        selectorExpression = MQConstants.SECKILL_SUCCESS_TAG,
        consumerGroup = MQConstants.SECKILL_SUCCESS_CONSUMER_GROUP
)
@Component
@Slf4j
public class SeckillSuccessMQListener implements RocketMQListener<SeckillSuccessMsg> {

    public void onMessage(SeckillSuccessMsg msg) {
        // 接收到秒杀订单成功消息, 通知客户端订单创建成功
        log.info("收到订单创建成功消息:{}", JSON.toJSONString(msg));

        try {
            Session session = WebSocketServer.clients.get(msg.getUuid());
            if (session != null) {
                String json = JSON.toJSONString(Result.success(msg));
                session.getBasicRemote().sendText(json);
            } else {
                log.warn("找不到:{}客户端连接!!!", msg.getUuid());
            }
        } catch (Exception e) {
            log.error("通知客户端出现异常!!", e);
        }
    }
}
