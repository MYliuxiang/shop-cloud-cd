package cn.wolfcode.cloud.shop.mq;

import cn.wolfcode.cloud.shop.ws.WebSocketServer;
import cn.wolfcode.shop.common.CodeMsg;
import cn.wolfcode.shop.common.Result;
import cn.wolfcode.shop.consts.MQConstants;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@RocketMQMessageListener(
        topic = MQConstants.SECKILL_ORDER_TOPIC,
        selectorExpression = MQConstants.SECKILL_FAILED_TAG,
        consumerGroup = MQConstants.SECKILL_FAILED_CONSUMER_GROUP
)
@Component
@Slf4j
public class SeckillFailedMQListener implements RocketMQListener<String> {

    public void onMessage(String uuid) {
        // 接收到秒杀订单成功消息, 通知客户端订单创建成功
        log.info("收到订单创建失败消息:{}", uuid);

        try {
            Session session = WebSocketServer.clients.get(uuid);
            if (session != null) {
                String json = JSON.toJSONString(Result.error(new CodeMsg(500310, "秒杀失败!")));
                session.getBasicRemote().sendText(json);
            } else {
                log.warn("找不到:{}客户端连接!!!", uuid);
            }
        } catch (Exception e) {
            log.error("通知客户端出现异常!!", e);
        }
    }
}
