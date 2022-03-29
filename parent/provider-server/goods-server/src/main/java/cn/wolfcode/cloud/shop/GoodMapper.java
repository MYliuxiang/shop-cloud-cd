package cn.wolfcode.cloud.shop;

import cn.wolfcode.cloud.shop.domain.Good;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Mapper
public interface GoodMapper {

    @SelectProvider(type = GoodSelectProvider.class, method = "selectGoodsByIdList")
    List<Good> selectGoodsByIdList(@Param("ids") Set<Long> ids);

    class GoodSelectProvider {

        public String selectGoodsByIdList(@Param("ids") Set<Long> ids) {
            // SQL 语句
            StringBuilder sql = new StringBuilder(100);
            sql.append("select * from t_goods");

            if (CollectionUtils.isEmpty(ids)) {
                return sql.toString();
            }

            // where id in (1,2,3)
            sql.append(" where id in(");

            for (Long id : ids) {
                sql.append(id).append(",");
            }

            // 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
            return sql.toString();
        }
    }
}
