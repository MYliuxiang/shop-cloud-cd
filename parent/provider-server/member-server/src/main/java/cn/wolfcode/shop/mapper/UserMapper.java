package cn.wolfcode.shop.mapper;

import cn.wolfcode.shop.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from t_user where id = #{id}")
    User selectByPrimaryKey(Long id);
}
