package cn.wolfcode.cloud.shop.mapper;

import cn.wolfcode.cloud.shop.domain.SeckillGood;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillGoodMapper {

    @Select("select * from t_seckill_goods")
    List<SeckillGood> listAll();

    @Select("select * from t_seckill_goods where id = #{id}")
    SeckillGood selectByPrimaryKey(Long id);

    // 版本号: stock_count
    // DML 语句操作后, 会返回当前执行的语句的受影响行数
    // 如果 where 条件不成立, 返回受影响的行数为 0
    @Update("update t_seckill_goods set stock_count = stock_count - 1 where id = #{seckillId} and stock_count > 0")
    int decrStcokCount(Long seckillId);

    @Update("update t_seckill_goods set stock_count = stock_count + 1 where id = #{seckillId}")
    void incrStockCount(Long seckillId);
}
