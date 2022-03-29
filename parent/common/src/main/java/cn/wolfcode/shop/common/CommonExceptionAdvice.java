package cn.wolfcode.shop.common;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class CommonExceptionAdvice {

    private CodeMsg defaultCodeMsg;

    public CommonExceptionAdvice(CodeMsg codeMsg) {
        this.defaultCodeMsg = codeMsg;
    }

    // 处理 BindException 参数异常
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result handleArgsException(BindException ex){
        ObjectError error = ex.getAllErrors().get(0);
        String defaultMessage = error.getDefaultMessage();

        return Result.error(CodeMsg.ARGS_ERROR.failArgs(defaultMessage));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleDefault(Exception ex){
        ex.printStackTrace();
        return Result.error(defaultCodeMsg);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result hanlderDefaultException(BusinessException e){
        return Result.error(e.getCodeMsg());
    }
}
