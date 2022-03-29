package cn.wolfcode.cloud.shop.web.feign.hystrix;

import cn.wolfcode.cloud.shop.domain.Good;
import cn.wolfcode.cloud.shop.web.feign.GoodFeignApi;
import cn.wolfcode.shop.common.Result;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class GoodFeignHystrix implements GoodFeignApi {

    public Result<List<Good>> getGoodListByIdList(Set<Long> idList) {
        return null;
    }
}
