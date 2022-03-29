package cn.wolfcode.cloud.shop.redis;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum SeckillRedisKey {

    SECKILL_GOODS_STOCK_COUNT_HASH("seckill_goods_stock_count_hash"),
    SECKILL_GOODS_HASH("seckill_goods_hash"),
    SECKILL_SUCCESS_USERS("seckill_success_users:");

    private String prefix;
    private Long expireTime;
    private TimeUnit unit;

    SeckillRedisKey(String prefix) {
        this(prefix, 0L, null);
    }

    SeckillRedisKey(String prefix, Long expireTime, TimeUnit unit) {
        this.prefix = prefix;
        this.expireTime = expireTime;
        this.unit = unit;
    }

    public String realKey() {
        return this.realKey("");
    }

    public String realKey(String key) {
        return this.prefix + key;
    }
}
