package cn.wolfcode.cloud.shop.web.feign;

import cn.wolfcode.cloud.shop.domain.Good;
import cn.wolfcode.cloud.shop.service.IGoodService;
import cn.wolfcode.shop.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class GoodFeignClient implements GoodFeignApi {

    @Autowired
    private IGoodService goodService;

    public Result<List<Good>> getGoodListByIdList(Set<Long> idList) {
        return Result.success(goodService.getGoodListByIdList(idList));
    }
}
