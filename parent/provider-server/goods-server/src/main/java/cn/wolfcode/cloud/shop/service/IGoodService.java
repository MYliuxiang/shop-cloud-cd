package cn.wolfcode.cloud.shop.service;

import cn.wolfcode.cloud.shop.domain.Good;

import java.util.List;
import java.util.Set;

public interface IGoodService {

    List<Good> getGoodListByIdList(Set<Long> idList);
}
