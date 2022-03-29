package cn.wolfcode.cloud.shop.mq.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderDelayMsg implements Serializable {

    private String orderNo;
    private Long seckillId;
}
