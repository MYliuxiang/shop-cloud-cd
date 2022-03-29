package cn.wolfcode.cloud.shop.service;

import cn.wolfcode.cloud.shop.vo.SeckillGoodVo;

import java.util.List;

public interface ISeckillGoodService {

    List<SeckillGoodVo> query();

    List<SeckillGoodVo> queryByCache();

    SeckillGoodVo findById(Long id);

    SeckillGoodVo findByCache(Long id);

    int decrStockCount(Long seckillId);

    void incrStockCount(Long seckillId);
}
