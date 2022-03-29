package cn.wolfcode.cloud.shop.web.feign;

import cn.wolfcode.cloud.shop.domain.Good;
import cn.wolfcode.cloud.shop.web.feign.hystrix.GoodFeignHystrix;
import cn.wolfcode.shop.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "goods-server", fallback = GoodFeignHystrix.class)
public interface GoodFeignApi {

    @RequestMapping("/getGoodListByIdList")
    Result<List<Good>> getGoodListByIdList(@RequestParam("idList") Set<Long> idList);
}