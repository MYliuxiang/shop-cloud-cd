package cn.wolfcode.shop.resolver;

import cn.wolfcode.shop.anno.UserParam;
import cn.wolfcode.shop.domain.User;
import cn.wolfcode.shop.redis.MemberRedisKey;
import cn.wolfcode.shop.util.CookieUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean supportsParameter(MethodParameter methodParameter) {
        // 判断当前参数的类型, 是否是 User.class
        return User.class.equals(methodParameter.getParameterType()) &&
                methodParameter.hasParameterAnnotation(UserParam.class);
    }

    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest req = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        if (req == null)
            return null;

        String token = CookieUtils.getCookieValue(req, CookieUtils.USER_TOKEN_NAME);
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        String json = stringRedisTemplate.opsForValue().get(MemberRedisKey.USER_TOKEN.realKey(token));
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        return JSON.parseObject(json, User.class);
    }
}
