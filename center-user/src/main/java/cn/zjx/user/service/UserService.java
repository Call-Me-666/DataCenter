package cn.zjx.user.service;

import cn.zjx.common.pojo.RequestResult;
import cn.zjx.user.pojo.UserInfo;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 10:16:23
 **/
public interface UserService {
    /**
     * 注册
     * @param userInfo 用户信息
     * @return 请求结果
     */
    RequestResult register(UserInfo userInfo);

    /**
     * 登录用户
     * @param userInfo
     * @return
     */
    RequestResult login(UserInfo userInfo);

    /**
     * 修改用户
     * @param userInfo
     * @return
     */
    RequestResult update(UserInfo userInfo);

    /**
     * 注销用户
     * @return
     */
    RequestResult unsubscribe(UserInfo userInfo);

    /**
     * 激活
     * @return
     */
    RequestResult active(String token);

    void test();
}
