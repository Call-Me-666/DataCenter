package cn.zjx.user.pojo;

import lombok.Data;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 10:14:54
 * @Version: 1.0
 **/
@Data
public class UserInfo {
    String uid;          // 唯一标识
    String name;        // 用户名
    String password;    // 密码
    String phone;
    String email;       // 邮箱
    byte[] image;       // 头像
    boolean isActive;   // 是否激活
}
