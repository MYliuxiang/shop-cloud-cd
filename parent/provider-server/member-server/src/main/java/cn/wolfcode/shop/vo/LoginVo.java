package cn.wolfcode.shop.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
public class LoginVo implements Serializable {

    @Pattern(regexp = "^1(3|4|5|6|7|8|9)\\d{9}$", message = "手机格式有误")
    private String mobile;
    private String password;
}
