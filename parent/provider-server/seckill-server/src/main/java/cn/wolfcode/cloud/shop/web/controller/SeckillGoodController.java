package cn.wolfcode.cloud.shop.web.controller;

import cn.wolfcode.cloud.shop.redis.SeckillRedisKey;
import cn.wolfcode.cloud.shop.service.ISeckillGoodService;
import cn.wolfcode.cloud.shop.vo.SeckillGoodVo;
import cn.wolfcode.shop.common.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seckillGoods")
public class SeckillGoodController {

    @Autowired
    private ISeckillGoodService seckillGoodService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 性能测试:
     *  1000 * 5: 5000
     *  优化前: QPS: 365/s
     * @return
     */
    @RequestMapping("/query")
    public Result<List<SeckillGoodVo>> query() {
        return Result.success(seckillGoodService.query());
    }

    @RequestMapping("/initData")
    public Result<String> initData() {
        // 模拟创建秒杀活动, 进行数据预热操作
        List<SeckillGoodVo> list = seckillGoodService.query();
        for (SeckillGoodVo vo : list) {
            stringRedisTemplate.opsForHash()
                    .put(SeckillRedisKey.SECKILL_GOODS_HASH.realKey(),
                            vo.getId() + "",
                                JSON.toJSONString(vo));

            stringRedisTemplate.opsForHash()
                    .put(SeckillRedisKey.SECKILL_GOODS_STOCK_COUNT_HASH.realKey(),
                    vo.getId() + "",
                    vo.getStockCount() + "");
        }
        return Result.success("success");
    }

    @RequestMapping("/findById")
    public Result<SeckillGoodVo> findById(Long seckillId) {
        return Result.success(seckillGoodService.findById(seckillId));
    }
}
