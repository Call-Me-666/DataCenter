package cn.zjx.user.controller;

import cn.zjx.common.pojo.RequestResult;
import cn.zjx.user.pojo.UserInfo;
import cn.zjx.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-20 13:58:29
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public RequestResult register(@RequestBody UserInfo userInfo){
        // 注册用户
        return userService.register(userInfo);
    }

    @GetMapping("/active/{token}")
    public RequestResult active(@PathVariable("token")String token){
        return userService.active(token);
    }

    @PostMapping("/login")
    public RequestResult login(@RequestBody UserInfo userInfo){
        return userService.login(userInfo);
    }

    @PostMapping("/unsubscribe")
    public RequestResult unsubscribe(@RequestBody UserInfo userInfo) {
        return userService.unsubscribe(userInfo);
    }

    @PostMapping("/update")
    public RequestResult update(@RequestBody UserInfo userInfo){
        return userService.update(userInfo);
    }

    @GetMapping("/test")
    public void Test(){
        userService.test();
    }
}
