package cn.wolfcode.shop.web.controller;

import cn.wolfcode.shop.anno.UserParam;
import cn.wolfcode.shop.common.Result;
import cn.wolfcode.shop.domain.User;
import cn.wolfcode.shop.service.IUserService;
import cn.wolfcode.shop.util.CookieUtils;
import cn.wolfcode.shop.util.DBUtil;
import cn.wolfcode.shop.util.MD5Util;
import cn.wolfcode.shop.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger("USER_CONTROLLER");

    @Autowired
    private IUserService userService;


    @RequestMapping("/login")
    public Result<String> login(@Valid LoginVo vo,
                                @CookieValue(required = false, name = CookieUtils.USER_TOKEN_NAME) String token,
                                HttpServletResponse resp) {
        String newToken = userService.login(vo);
        // 将 token 响应到 cookie 中
        CookieUtils.addCookie(resp, CookieUtils.USER_TOKEN_NAME, newToken);
        return Result.success(newToken);
    }

    @RequestMapping("/current")
    public Result<User> current(@UserParam User user) {
        System.out.println("当前用户:" + user.getId());
        return Result.success(user);
    }

    @RequestMapping("/findById")
    public User findById(Long id) {
        return userService.findById(id);
    }

    @RequestMapping("/initData")
    public Result<String> initData() throws Exception {
        List<User> users = initUser(1000);//在内存中创建500个User对象
        //insertToDb(users);//把500个User对象保存到数据库中
        createToken(users);//通过500个User对象，调用service方法，模拟登陆，创建500个token,然后存在磁盘文件中.(jmeter)
        return Result.success("");
    }

    private void createToken(List<User> users) throws Exception {
        File file = new File("D:/tokens.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            LoginVo vo = new LoginVo();
            vo.setMobile(users.get(i).getId() + "");
            String password = MD5Util.inputPass2FormPass("111111");
            vo.setPassword(password);
            String token = userService.login(vo);
            String row = users.get(i).getId() + "," + token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();
    }

    private void insertToDb(List<User> users) throws Exception {
        Connection conn = DBUtil.getConn();
        String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
    }

    private List<User> initUser(int count) {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setLoginCount(1);
            user.setNickname("user" + i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2c3d4e");
            String inputPass = MD5Util.inputPass2FormPass("111111");
            user.setPassword(MD5Util.formPass2DbPass(inputPass, user.getSalt()));
            users.add(user);
        }
        return users;
    }
}
