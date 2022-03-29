package cn.wolfcode.shop.redis;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum MemberRedisKey {
    USER_TOKEN("userToken:", 30L, TimeUnit.MINUTES);

    private String prefix;
    private Long expireTime;
    private TimeUnit unit;

    MemberRedisKey(String prefix) {
        this(prefix, 0L, null);
    }

    MemberRedisKey(String prefix, Long expireTime, TimeUnit unit) {
        this.prefix = prefix;
        this.expireTime = expireTime;
        this.unit = unit;
    }

    public String realKey(String key) {
        return this.prefix + key;
    }
}
