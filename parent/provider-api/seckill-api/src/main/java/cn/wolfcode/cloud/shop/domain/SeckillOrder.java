package cn.wolfcode.cloud.shop.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by wolfcode-liugang
 */
@Setter
@Getter
public class SeckillOrder implements Serializable {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long seckillId;
}
