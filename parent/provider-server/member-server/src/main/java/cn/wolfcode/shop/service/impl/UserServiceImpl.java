package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.common.BusinessException;
import cn.wolfcode.shop.domain.User;
import cn.wolfcode.shop.mapper.UserMapper;
import cn.wolfcode.shop.msg.MemberServerCodeMsg;
import cn.wolfcode.shop.redis.MemberRedisKey;
import cn.wolfcode.shop.service.IUserService;
import cn.wolfcode.shop.util.MD5Util;
import cn.wolfcode.shop.vo.LoginVo;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public User findById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public String login(LoginVo vo) {
        // 1. 校验用户参数是否正常

        // 2. 根据用户名查询出对应的用户,如果查询不到提示用户名或密码错误
        User user = this.findById(Long.parseLong(vo.getMobile()));
        if (user == null) {
            throw new BusinessException(MemberServerCodeMsg.USERNAME_OR_PASSWORD_ERROR);
        }
        // 3. 如果查询到用户, 判断密码是否正确, 如果密码不正确,提示用户名或密码错误
        String dbPassword = user.getPassword();
        // 此时的密码,是 inputPass2FormPass 加密后的密文
        String frontendPassword = MD5Util.formPass2DbPass(vo.getPassword(), user.getSalt());
        if (!frontendPassword.equals(dbPassword)) {
            throw new BusinessException(MemberServerCodeMsg.USERNAME_OR_PASSWORD_ERROR);
        }
        // 4. 用户登录成功, 生成 token, 并将 token 以及当前用户保存到 redis 中
        String token = this.createToken(user);
        // 5. 返回生成的 token 给 controller
        return token;
    }

    public boolean refreshToken(String token) {
        // 直接调用 redis 的刷新有效时间方法: expire
        Boolean expire = redisTemplate.expire(
                MemberRedisKey.USER_TOKEN.realKey(token),
                MemberRedisKey.USER_TOKEN.getExpireTime(),
                MemberRedisKey.USER_TOKEN.getUnit());

        return expire != null && expire;
    }

    private String createToken(User user) {
        // 生成 token
        String token = "";
        token = UUID.randomUUID().toString().replaceAll("-", "");
        // 将 token 保存到 redis 中
        redisTemplate.opsForValue().set(
                MemberRedisKey.USER_TOKEN.realKey(token),
                JSON.toJSONString(user),
                MemberRedisKey.USER_TOKEN.getExpireTime(),
                MemberRedisKey.USER_TOKEN.getUnit());

        // 返回 token
        return token;
    }
}
