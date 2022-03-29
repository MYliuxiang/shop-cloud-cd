package cn.wolfcode.shop.util;


import cn.wolfcode.shop.common.BusinessException;
import cn.wolfcode.shop.common.CodeMsg;
import org.springframework.util.StringUtils;

/**
 * 参数断言判断
 */
public class AssertUtils {


    /**
     * 判断指定value参数是否有值, 如果没有抛出异常, 信息: msg
     * @param v
     * @param codeMsg
     */
    public static void hasLength(String v, CodeMsg codeMsg) {

        if(!StringUtils.hasLength(v)){
            throw new BusinessException(codeMsg);
        }

    }

    /**
     * 判断传入的2个参数是否相等
     * @param v1
     * @param v2
     * @param codeMsg
     */
    public static void isEquals(String v1 , String v2, CodeMsg codeMsg) {

        if(v1 == null || v2 == null){
            throw new RuntimeException("传入参数不能为null");
        }

        if(!v1.equals(v2)){
            throw new BusinessException(codeMsg);
        }
    }
}
