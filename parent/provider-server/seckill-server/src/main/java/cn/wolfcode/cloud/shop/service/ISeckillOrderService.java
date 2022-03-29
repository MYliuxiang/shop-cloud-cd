package cn.wolfcode.cloud.shop.service;

import cn.wolfcode.cloud.shop.domain.SeckillOrder;

public interface ISeckillOrderService {
    SeckillOrder findBySeckillIdAndUserId(Long seckillId, Long id);

    void createSeckillOrder(Long seckillId, Long userId, String orderNo);
}
