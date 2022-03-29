package cn.wolfcode.cloud.shop.web.advice;

import cn.wolfcode.shop.common.CodeMsg;
import cn.wolfcode.shop.common.CommonExceptionAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SeckillServerControllerAdvice extends CommonExceptionAdvice {

    public SeckillServerControllerAdvice() {
        super(CodeMsg.DEFAULT_ERROR);
    }
}
