package cn.wolfcode.cloud.shop.mapper;

import cn.wolfcode.cloud.shop.domain.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderInfoMapper {

    @Insert("insert into t_order_info(order_no,user_id,good_id,delivery_addr_id,good_name,good_img,good_count,good_price,seckill_price,status,create_date,pay_date) values(#{orderNo},#{userId},#{goodId},#{deliveryAddrId},#{goodName},#{goodImg},#{goodCount},#{goodPrice},#{seckillPrice},#{status},#{createDate},#{payDate})")
    void insert(OrderInfo orderInfo);

    @Select("select * from t_order_info where order_no = #{orderNo}")
    OrderInfo selectByPrimaryKey(String orderNo);

    /**
     * 通过乐观锁的方式, 直接判断订单状态为未支付, 既可以判断订单是否已超时, 也可以在订单确实超时的情况直接修改订单
     * 最终得到的响应结果 受影响的行数
     *  如果 == 0:修改失败, 表示订单已支付
     *  如果 > 0:修改成功, 就表示订单已超时
     * @param orderNo
     * @return
     */
    @Update("update t_order_info set status = 4 where order_no = #{orderNo} and status = 0")
    int updateTimeoutStatus(String orderNo);
}
