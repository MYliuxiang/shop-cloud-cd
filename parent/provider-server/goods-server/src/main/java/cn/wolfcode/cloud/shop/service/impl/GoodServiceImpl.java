package cn.wolfcode.cloud.shop.service.impl;

import cn.wolfcode.cloud.shop.GoodMapper;
import cn.wolfcode.cloud.shop.domain.Good;
import cn.wolfcode.cloud.shop.service.IGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
public class GoodServiceImpl implements IGoodService {

    @Autowired
    private GoodMapper goodMapper;

    public List<Good> getGoodListByIdList(Set<Long> idList) {
        if (CollectionUtils.isEmpty(idList))
            return null;

        return goodMapper.selectGoodsByIdList(idList);
    }
}
