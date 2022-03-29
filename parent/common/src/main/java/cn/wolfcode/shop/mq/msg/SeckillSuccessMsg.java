package cn.wolfcode.shop.mq.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeckillSuccessMsg implements Serializable {

    /* 用于通知客户端结果的id */
    private String uuid;
    /* 用于让客户端进行页面跳转的订单id */
    private String orderNo;
}
