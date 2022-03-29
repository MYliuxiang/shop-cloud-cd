package cn.wolfcode.shop.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.text.MessageFormat;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CodeMsg implements Serializable {
    private Integer code;
    private String msg;

    public static final CodeMsg DEFAULT_ERROR = new CodeMsg(500500,"服务器异常");
    public static final CodeMsg ARGS_ERROR = new CodeMsg(500001,"参数有误:{0}");

    public CodeMsg failArgs(String msg) {
        return new CodeMsg(this.getCode(), MessageFormat.format(this.getMsg(), msg));
    }
}
