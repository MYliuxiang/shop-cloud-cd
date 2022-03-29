package cn.wolfcode.cloud.shop.service.impl;

import cn.wolfcode.cloud.shop.domain.Good;
import cn.wolfcode.cloud.shop.domain.SeckillGood;
import cn.wolfcode.cloud.shop.mapper.SeckillGoodMapper;
import cn.wolfcode.cloud.shop.redis.SeckillRedisKey;
import cn.wolfcode.cloud.shop.service.ISeckillGoodService;
import cn.wolfcode.cloud.shop.vo.SeckillGoodVo;
import cn.wolfcode.cloud.shop.web.feign.GoodFeignApi;
import cn.wolfcode.shop.common.Result;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SeckillGoodServiceImpl implements ISeckillGoodService {

    private final Logger log = LoggerFactory.getLogger("SECKILL_GOOD_SERVICE");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SeckillGoodMapper seckillGoodMapper;
    @Autowired
    private GoodFeignApi goodFeignApi;

    public SeckillGoodVo findById(Long id) {
        // 1. 查询秒杀商品列表
        SeckillGood sg = seckillGoodMapper.selectByPrimaryKey(id);
        List<SeckillGood> seckillGoodList = Collections.singletonList(sg);

        List<SeckillGoodVo> ret = join(seckillGoodList);
        return ret != null && ret.size() > 0 ? ret.get(0) : null;
    }

    public SeckillGoodVo findByCache(Long id) {
        String json = (String) stringRedisTemplate.opsForHash().get(SeckillRedisKey.SECKILL_GOODS_HASH.realKey(), id + "");
        if (!StringUtils.isEmpty(json)) {
            SeckillGoodVo vo = JSON.parseObject(json, SeckillGoodVo.class);

            // 同步 Redis 库存信息
            String stockCountStr = (String) stringRedisTemplate.opsForHash().get(SeckillRedisKey.SECKILL_GOODS_STOCK_COUNT_HASH.realKey(), id + "");
            if (!StringUtils.isEmpty(stockCountStr)) {
                Integer stockCount = Integer.parseInt(stockCountStr);
                stockCount = stockCount < 0 ? 0 : stockCount;
                vo.setStockCount(stockCount);
            }

            return vo;
        }

        return null;
    }

    public int decrStockCount(Long seckillId) {
        return seckillGoodMapper.decrStcokCount(seckillId);
    }

    public void incrStockCount(Long seckillId) {
        seckillGoodMapper.incrStockCount(seckillId);
    }

    public List<SeckillGoodVo> queryByCache() {
        List<Object> values = stringRedisTemplate.opsForHash().values(SeckillRedisKey.SECKILL_GOODS_HASH.realKey());
        if (!CollectionUtils.isEmpty(values)) {
            List<SeckillGoodVo> ret = new ArrayList<>(values.size());
            for (Object value : values) {
                String json = (String) value;
                SeckillGoodVo vo = JSON.parseObject(json, SeckillGoodVo.class);

                // 同步 Redis 库存信息
                String stockCountStr = (String) stringRedisTemplate.opsForHash().get(SeckillRedisKey.SECKILL_GOODS_STOCK_COUNT_HASH.realKey(), vo.getId() + "");
                if (!StringUtils.isEmpty(stockCountStr)) {
                    Integer stockCount = Integer.parseInt(stockCountStr);
                    stockCount = stockCount < 0 ? 0 : stockCount;
                    vo.setStockCount(stockCount);
                }

                ret.add(vo);
            }

            return ret;
        }
        return null;
    }

    public List<SeckillGoodVo> query() {
        // 1. 查询秒杀商品列表
        List<SeckillGood> seckillGoodList = seckillGoodMapper.listAll();
        return join(seckillGoodList);
    }

    private List<SeckillGoodVo> join(List<SeckillGood> seckillGoodList) {
        // 2. 根据查询到的秒杀商品列表, 得到一个商品id列表
        Set<Long> goodIdList = new HashSet<>(seckillGoodList.size());
        for (SeckillGood seckillGood : seckillGoodList) {
            goodIdList.add(seckillGood.getId());
        }
        // 3. 通过 Feign 远程调用商品服务, 得到一个商品列表
        Result<List<Good>> result = goodFeignApi.getGoodListByIdList(goodIdList);
        // 此时有可能返回三种结果:
        // feign 调用出现异常或超时, 降级结果:返回null
        // 调用方法出现异常,进入advice返回 Result 对象 code != 200
        if (result == null) {
            // 返回 null 或者直接抛出异常, 给予调用者提示信息
            return null;
        }
        if (result.hasError()) {
            log.warn("获取商品列表出现异常: code={}, msg={}", result.getCode(), result.getMsg());
            return null;
        }
        // 正常调用, 返回结果
        List<Good> goodList = result.getData();
        Map<Long, Good> tempCache = new HashMap<>(goodList.size());
        for (Good good : goodList) {
            tempCache.put(good.getId(), good);
        }
        // 4. 聚合秒杀商品列表和商品列表, 得到一个秒杀商品vo列表
        List<SeckillGoodVo> ret = new ArrayList<>(seckillGoodList.size());
        for (SeckillGood seckillGood : seckillGoodList) {
            Good good = tempCache.get(seckillGood.getGoodId());
            SeckillGoodVo vo = new SeckillGoodVo();

            if (good != null) {
                BeanUtils.copyProperties(good, vo);
            }

            // 秒杀商品属性
            BeanUtils.copyProperties(seckillGood, vo);

            ret.add(vo);
        }

        return ret;
    }
}
