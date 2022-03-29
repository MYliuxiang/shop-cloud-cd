package cn.wolfcode.shop.filter;

import cn.wolfcode.shop.common.Result;
import cn.wolfcode.shop.feign.UserFeignApi;
import cn.wolfcode.shop.util.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class TokenRefreshFilter extends ZuulFilter {

    @Autowired
    private UserFeignApi userFeignApi;

    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    public int filterOrder() {
        return 0;
    }

    public boolean shouldFilter() {
        // 判断是否有 cookie, 再判断 cookie 中是否有 token
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = CookieUtils.getCookieValue(request, CookieUtils.USER_TOKEN_NAME);
        return !StringUtils.isEmpty(token);
    }

    public Object run() throws ZuulException {
        // 执行 token 刷新操作
        // 先通过上下文得到 token
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = CookieUtils.getCookieValue(request, CookieUtils.USER_TOKEN_NAME);
        // 远程调用会员服务的刷新 token 方法
        Result<Boolean> result = userFeignApi.refreshToken(token);
        if (!result.hasError() && result.getData()) {
            // 刷新 cookie 有效时间
            CookieUtils.addCookie(ctx.getResponse(),
                    CookieUtils.USER_TOKEN_NAME, token);
        }
        return null;
    }
}
