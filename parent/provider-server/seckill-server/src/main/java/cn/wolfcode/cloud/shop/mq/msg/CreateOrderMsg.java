package cn.wolfcode.cloud.shop.mq.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 创建订单消息对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderMsg implements Serializable {

    private Long seckillId;
    private Long userId;
    private String uuid;
}
