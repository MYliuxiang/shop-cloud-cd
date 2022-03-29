package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.User;
import cn.wolfcode.shop.vo.LoginVo;

public interface IUserService {

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    User findById(Long id);

    String login(LoginVo vo);

    boolean refreshToken(String token);
}
