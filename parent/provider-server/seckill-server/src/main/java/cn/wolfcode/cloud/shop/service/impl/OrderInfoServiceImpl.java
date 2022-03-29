package cn.wolfcode.cloud.shop.service.impl;

import cn.wolfcode.cloud.shop.domain.OrderInfo;
import cn.wolfcode.cloud.shop.mapper.OrderInfoMapper;
import cn.wolfcode.cloud.shop.msg.SeckillServerCodeMsg;
import cn.wolfcode.cloud.shop.redis.SeckillRedisKey;
import cn.wolfcode.cloud.shop.service.IOrderInfoService;
import cn.wolfcode.cloud.shop.service.ISeckillGoodService;
import cn.wolfcode.cloud.shop.service.ISeckillOrderService;
import cn.wolfcode.cloud.shop.util.IdGenerateUtil;
import cn.wolfcode.cloud.shop.vo.SeckillGoodVo;
import cn.wolfcode.shop.common.BusinessException;
import cn.wolfcode.shop.consts.MQConstants;
import cn.wolfcode.shop.domain.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    @Autowired
    private ISeckillGoodService seckillGoodService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Transactional(rollbackFor = Exception.class)
    public String doSeckill(Long seckillId, Long userId) {
        // 扣减秒杀商品库存: t_seckill_goods, stockCount=1
        int ret = seckillGoodService.decrStockCount(seckillId);
        if (ret <= 0) {
            // 修改失败, 提示库存不足
            throw new BusinessException(SeckillServerCodeMsg.STOCK_COUNT_OVER_ERROR);
        }
        // 创建基础订单: t_order_info
        String orderNo = this.createOrder(seckillId, userId);
        // 创建秒杀订单: t_seckill_order
        seckillOrderService.createSeckillOrder(seckillId, userId, orderNo);
        return orderNo;
    }

    public OrderInfo findById(String orderNo, User user) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderNo);
        if (!user.getId().equals(orderInfo.getUserId())) {
            throw new BusinessException(SeckillServerCodeMsg.OP_ERROR);
        }
        return orderInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkOrderTimeOut(String orderNo, Long seckillId) {
        // 1. 检查订单是否超时未支付
        // 2. 如果已经超时: 需要将当前订单状态修改为超时未支付 update o set status = 4 where status = 0
        int ret = orderInfoMapper.updateTimeoutStatus(orderNo);
        //
        if (ret > 0) {
            // 2.2 回补 MySQL 库存
            seckillGoodService.incrStockCount(seckillId);

            // 2.3 将 mysql 中的库存同步到 redis 中 + 1
            this.incrStockCountForRedis(seckillId);

            // 2.4 清除本地售完标记
            // 3. 发送广播消息, 通知集群中的所有节点清除自身的本地售完标记
            rocketMQTemplate.syncSend(MQConstants.CLEAR_STOCK_COUNT_FLAG_DEST, seckillId);
        }
    }

    public void handlerFailedSeckill(Long seckillId) {
        // 回补 redis 库存
        this.incrStockCountForRedis(seckillId);
        // 清除本地售完标记
        rocketMQTemplate.syncSend(MQConstants.CLEAR_STOCK_COUNT_FLAG_DEST, seckillId);
    }

    private void incrStockCountForRedis(Long seckillId) {
        Object stockCountInRedis =  stringRedisTemplate.opsForHash()
                .get(SeckillRedisKey.SECKILL_GOODS_STOCK_COUNT_HASH.realKey(), seckillId + "");

        Integer stockCount = null;
        int updateStockCount = 1;
        if (stockCountInRedis != null) {
            if (stockCountInRedis instanceof String) {
                stockCount = Integer.parseInt((String) stockCountInRedis);
            } else {
                stockCount = (Integer) stockCountInRedis;
            }

            if (stockCount > 0) {
                // 如果 redis 中还有库存, 库存等于源库存 + 1
                updateStockCount = updateStockCount + stockCount;
            }
        }

        // 如果 stockCount < 0 或者 str 为空, 这样我们都将redis的库存设置为 1
        stringRedisTemplate.opsForHash()
                .put(SeckillRedisKey.SECKILL_GOODS_STOCK_COUNT_HASH.realKey(),
                        seckillId + "", updateStockCount + "");
    }

    private String createOrder(Long seckillId, Long userId) {
        SeckillGoodVo vo = seckillGoodService.findByCache(seckillId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setSeckillPrice(vo.getSeckillPrice());
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodCount(1);// 商品数量
        orderInfo.setGoodId(vo.getGoodId());
        orderInfo.setGoodImg(vo.getGoodImg());
        orderInfo.setGoodName(vo.getGoodName());
        orderInfo.setGoodPrice(vo.getGoodPrice());
        String orderNo = "";
        // 分布式唯一主键
        long id = IdGenerateUtil.get().nextId();
        orderNo = orderNo + id;
        orderInfo.setOrderNo(orderNo); // TODO
        orderInfo.setUserId(userId);
        // 保存订单对象
        orderInfoMapper.insert(orderInfo);
        return orderNo;
    }
}
