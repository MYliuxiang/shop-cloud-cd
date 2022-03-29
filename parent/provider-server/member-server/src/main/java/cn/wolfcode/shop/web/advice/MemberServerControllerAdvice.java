package cn.wolfcode.shop.web.advice;

import cn.wolfcode.shop.common.CommonExceptionAdvice;
import cn.wolfcode.shop.msg.MemberServerCodeMsg;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class MemberServerControllerAdvice extends CommonExceptionAdvice {

    public MemberServerControllerAdvice() {
        super(MemberServerCodeMsg.DEFAULT_ERROR);
    }
}
