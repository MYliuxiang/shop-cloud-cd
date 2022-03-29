package cn.wolfcode.shop.web.feign;

import cn.wolfcode.shop.common.Result;
import cn.wolfcode.shop.feign.UserFeignApi;
import cn.wolfcode.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFeignClient implements UserFeignApi {

    @Autowired
    private IUserService userService;

    public Result<Boolean> refreshToken(String token) {
        return Result.success(userService.refreshToken(token));
    }
}
