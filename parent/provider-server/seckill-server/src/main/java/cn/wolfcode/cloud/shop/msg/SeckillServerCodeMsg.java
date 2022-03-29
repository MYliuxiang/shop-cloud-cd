package cn.wolfcode.cloud.shop.msg;

import cn.wolfcode.shop.common.CodeMsg;

public class SeckillServerCodeMsg extends CodeMsg {

    public static SeckillServerCodeMsg OP_ERROR = new SeckillServerCodeMsg(500301, "非法操作");
    public static SeckillServerCodeMsg NOT_START_ERROR = new SeckillServerCodeMsg(500302, "秒杀活动尚未开启");
    public static SeckillServerCodeMsg SECKILL_END = new SeckillServerCodeMsg(500303, "秒杀活动已经结束");
    public static SeckillServerCodeMsg AREADY_CREATE_ORDER_ERROR = new SeckillServerCodeMsg(500304, "请不要重复下单");
    public static SeckillServerCodeMsg STOCK_COUNT_OVER_ERROR = new SeckillServerCodeMsg(500305, "你来晚了,库存已售完");

    private SeckillServerCodeMsg(int code, String msg) {
        super(code, msg);
    }
}
