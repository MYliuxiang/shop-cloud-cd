package cn.wolfcode.cloud.shop.service;

import cn.wolfcode.cloud.shop.domain.OrderInfo;
import cn.wolfcode.shop.domain.User;

public interface IOrderInfoService {

    String doSeckill(Long seckillId, Long userId);

    OrderInfo findById(String orderNo, User user);

    void checkOrderTimeOut(String orderNo, Long seckillId);

    void handlerFailedSeckill(Long seckillId);
}
