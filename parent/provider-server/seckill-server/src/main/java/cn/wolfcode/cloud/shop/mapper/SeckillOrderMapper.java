package cn.wolfcode.cloud.shop.mapper;

import cn.wolfcode.cloud.shop.domain.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillOrderMapper {

    @Select("select * from t_seckill_order where seckill_id = #{seckillId} and user_id = #{userId}")
    SeckillOrder selectBySeckillIdAndUserId(@Param("seckillId") Long seckillId, @Param("userId") Long userId);

    @Insert("insert into t_seckill_order(order_no, user_id, seckill_id) values(#{orderNo}, #{userId}, #{seckillId})")
    void insert(SeckillOrder order);
}
