package cn.wolfcode.cloud.shop.web.controller;

import cn.wolfcode.cloud.shop.domain.OrderInfo;
import cn.wolfcode.cloud.shop.mq.msg.CreateOrderMsg;
import cn.wolfcode.cloud.shop.msg.SeckillServerCodeMsg;
import cn.wolfcode.cloud.shop.redis.SeckillRedisKey;
import cn.wolfcode.cloud.shop.service.IOrderInfoService;
import cn.wolfcode.cloud.shop.service.ISeckillGoodService;
import cn.wolfcode.cloud.shop.service.ISeckillOrderService;
import cn.wolfcode.cloud.shop.vo.SeckillGoodVo;
import cn.wolfcode.shop.anno.UserParam;
import cn.wolfcode.shop.common.BusinessException;
import cn.wolfcode.shop.common.Result;
import cn.wolfcode.shop.consts.MQConstants;
import cn.wolfcode.shop.domain.User;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/orders")
public class OrderInfoController {

    public static final ConcurrentHashMap<Long, Boolean> STOCK_COUNT_MAP = new ConcurrentHashMap<>();

    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private ISeckillGoodService seckillGoodService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @RequestMapping("/{orderNo}")
    public Result<OrderInfo> findById(@PathVariable("orderNo") String orderNo, @UserParam User user) {
        OrderInfo orderInfo = orderInfoService.findById(orderNo, user);
        return Result.success(orderInfo);
    }

    /**
     * 压力测试
     * 1000 * 4: 4000
     * 优化前:
     * TPS: 267/s
     * 优化后:
     * TPS: 879/s
     * 100 * 50: 5000
     * TPS: 1146/s
     *
     * @param seckillId
     * @param user
     * @return
     */
    @RequestMapping("/doSeckill")
    public Result<String> doSeckill(Long seckillId, String uuid,
                                    @UserParam User user) {
        // 校验逻辑
        if (user == null || seckillId == null || StringUtils.isEmpty(uuid)) {
            throw new BusinessException(SeckillServerCodeMsg.OP_ERROR);
        }
        // 判断商品是否已售完
        Boolean stockCountFlag = STOCK_COUNT_MAP.get(seckillId);
        if (stockCountFlag != null && stockCountFlag) {
            throw new BusinessException(SeckillServerCodeMsg.STOCK_COUNT_OVER_ERROR);
        }
        // 获取秒杀商品对象, 判断秒杀活动是否存在
        // req:10 -> vo: stockCount: 1
        SeckillGoodVo vo = seckillGoodService.findByCache(seckillId);
        if (vo == null) {
            throw new BusinessException(SeckillServerCodeMsg.OP_ERROR);
        }
        // 判断当前时间是否在活动范围内
        Date now = new Date();
        // < 0 表示当前时间小于开始时间,活动尚未开启
        if (now.compareTo(vo.getStartDate()) < 0) {
            throw new BusinessException(SeckillServerCodeMsg.NOT_START_ERROR);
        }
        // 当前时间>=结束时间,表示活动已经结束
        if (now.compareTo(vo.getEndDate()) >= 0) {
            throw new BusinessException(SeckillServerCodeMsg.SECKILL_END);
        }
        // 判断用户是否已经对该秒杀下过单: t_seckill_order
//        SeckillOrder seckillOrder = seckillOrderService.findBySeckillIdAndUserId(seckillId, user.getId());
        Boolean member = stringRedisTemplate.opsForSet().isMember(SeckillRedisKey.SECKILL_SUCCESS_USERS.realKey(seckillId + ""),
                user.getId() + "");
        if (member != null && member) {
            throw new BusinessException(SeckillServerCodeMsg.AREADY_CREATE_ORDER_ERROR);
        }
        // 判断秒杀商品库存是否足够
//        if (vo.getStockCount() <= 0) {
//            throw new BusinessException(SeckillServerCodeMsg.STOCK_COUNT_OVER_ERROR);
//        }
        // 如果当前库存为1, 那么一个请求进来,减完以后返回的结果 = 0
        Long stockCount = stringRedisTemplate.opsForHash().increment(SeckillRedisKey.SECKILL_GOODS_STOCK_COUNT_HASH.realKey(),
                seckillId + "",
                -1
        );
        // 当预减完以后的值, 小于 0 时,就意味着库存已经不足了
        if (stockCount < 0) {
            // 添加本地售完标记
            STOCK_COUNT_MAP.put(seckillId, true);
            throw new BusinessException(SeckillServerCodeMsg.STOCK_COUNT_OVER_ERROR);
        }
        // 创建订单/扣减库存....> service
        // String orderNo = orderInfoService.doSeckill(seckillId, user.getId());
        // 发送 rocketMq 消息
        try {
            rocketMQTemplate.syncSend(MQConstants.CREATE_ORDER_DEST,
                    new CreateOrderMsg(seckillId, user.getId(), uuid));
        } catch (Exception e) {
            // 如果发送消息失败了, 需要设计重试机制
            e.printStackTrace();
        }
        return Result.success("正在秒杀当中, 请稍等片刻");
    }
}
