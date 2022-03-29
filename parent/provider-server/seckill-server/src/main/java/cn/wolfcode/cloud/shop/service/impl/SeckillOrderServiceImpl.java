package cn.wolfcode.cloud.shop.service.impl;

import cn.wolfcode.cloud.shop.domain.SeckillOrder;
import cn.wolfcode.cloud.shop.mapper.SeckillOrderMapper;
import cn.wolfcode.cloud.shop.redis.SeckillRedisKey;
import cn.wolfcode.cloud.shop.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillOrderServiceImpl implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public SeckillOrder findBySeckillIdAndUserId(Long seckillId, Long userId) {
        return seckillOrderMapper.selectBySeckillIdAndUserId(seckillId, userId);
    }

    public void createSeckillOrder(Long seckillId, Long userId, String orderNo) {
        SeckillOrder order = new SeckillOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSeckillId(seckillId);
        seckillOrderMapper.insert(order);
        // 当保存成功以后, 标记当前用户已经下过单了
        // 使用 SET 结构保存当前秒杀已经下过单的用户id
        stringRedisTemplate.opsForSet().add(SeckillRedisKey.SECKILL_SUCCESS_USERS.realKey(seckillId+""), userId + "");
    }
}
