package cn.wolfcode.shop.feign;

import cn.wolfcode.shop.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("member-server")
public interface UserFeignApi {

    @RequestMapping("/refreshToken")
    Result<Boolean> refreshToken(@RequestParam("token") String token);
}
