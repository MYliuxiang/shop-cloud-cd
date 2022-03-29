package cn.wolfcode.shop.msg;

import cn.wolfcode.shop.common.CodeMsg;

import java.text.MessageFormat;

/**
 * 定义这个服务中错误状态码和消息的映射
 */
public class MemberServerCodeMsg extends CodeMsg {


    public MemberServerCodeMsg(Integer code,String msg){
        super(code,msg);
    }
    public static final MemberServerCodeMsg DEFAULT_ERROR = new MemberServerCodeMsg(500100,"会员服务繁忙");
    public static final MemberServerCodeMsg USERNAME_OR_PASSWORD_ERROR = new MemberServerCodeMsg(500103,"用户名或密码错误");

    public static void main(String[] args) {
        String msg = "参数有误:{0}";
        System.out.println(MessageFormat.format(msg, "用户名不能为空!"));
    }
}
