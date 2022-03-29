package cn.wolfcode.shop.consts;

/**
 * MQ的常量类
 */
public interface MQConstants {

    /* 秒杀订单主题: 所有的秒杀订单相关的消息都发送在这个主题下面 */
    String SECKILL_ORDER_TOPIC = "SECKILL_ORDER_TOPIC";

    /* 创建订单相关的常量 */
    String CREATE_ORDER_TAG = "CREATE_ORDER_TAG";
    /* 发送创建订单消息的目的地 */
    String CREATE_ORDER_DEST = SECKILL_ORDER_TOPIC + ":" + CREATE_ORDER_TAG;
    /* 创建订单的消费者组 */
    String CREATE_ORDER_CONSUMER_GROUP = "CREATE_ORDER_CONSUMER_GROUP";

    /* 创建订单成功相关的常量 */
    /* 创建订单成功消息过滤标签 */
    String SECKILL_SUCCESS_TAG = "SECKILL_SUCCESS_TAG";
    /* 发送秒杀订单成功的目的地 */
    String SECKILL_SUCCESS_DEST = SECKILL_ORDER_TOPIC + ":" + SECKILL_SUCCESS_TAG;
    /* 创建秒杀订单成功的消费者组 */
    String SECKILL_SUCCESS_CONSUMER_GROUP = "SECKILL_SUCCESS_CONSUMER_GROUP";

    /* 创建订单失败相关的常量 */
    /* 创建订单失败消息过滤标签 */
    String SECKILL_FAILED_TAG = "SECKILL_FAILED_TAG";
    /* 发送秒杀订单失败的目的地 */
    String SECKILL_FAILED_DEST = SECKILL_ORDER_TOPIC + ":" + SECKILL_FAILED_TAG;
    /* 创建秒杀订单失败的消费者组 */
    String SECKILL_FAILED_CONSUMER_GROUP = "SECKILL_FAILED_CONSUMER_GROUP";

    /* 创建订单成功的延迟消息相关的常量 */
    /* 创建订单成功的延迟消息过滤标签 */
    String SECKILL_SUCCESS_DELAY_TAG = "SECKILL_SUCCESS_DELAY_TAG";
    /* 发送秒杀订单成功的延迟消息的目的地 */
    String SECKILL_SUCCESS_DELAY_DEST = SECKILL_ORDER_TOPIC + ":" + SECKILL_SUCCESS_DELAY_TAG;
    /* 创建秒杀订单成功的延迟消息的消费者组 */
    String SECKILL_SUCCESS_DELAY_CONSUMER_GROUP = "SECKILL_SUCCESS_DELAY_CONSUMER_GROUP";

    /* 清除本地售完标记相关的常量 */
    /* 清除本地售完标记消息过滤标签 */
    String CLEAR_STOCK_COUNT_FLAG_TAG = "CLEAR_STOCK_COUNT_FLAG_TAG";
    /* 发送清除本地售完标记消息的目的地 */
    String CLEAR_STOCK_COUNT_FLAG_DEST = SECKILL_ORDER_TOPIC + ":" + CLEAR_STOCK_COUNT_FLAG_TAG;
    /* 创建清除本地售完标记消息的消费者组 */
    String CLEAR_STOCK_COUNT_FLAG_CONSUMER_GROUP = "CLEAR_STOCK_COUNT_FLAG_CONSUMER_GROUP";
}
