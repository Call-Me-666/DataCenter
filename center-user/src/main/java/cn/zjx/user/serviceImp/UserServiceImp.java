package cn.zjx.user.serviceImp;

import cn.zjx.common.jwt.util.JwtUtil;
import cn.zjx.common.pojo.RequestResult;
import cn.zjx.user.dao.UserDao;
import cn.zjx.user.pojo.UserInfo;
import cn.zjx.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 10:52:25
 * @Version: 1.0
 **/
public class UserServiceImp implements UserService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserDao userDao;
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public RequestResult register(UserInfo userInfo) {
        RequestResult requestResult = new RequestResult();
        // 判断用户名和密码
        if(StringUtils.isNotBlank(userInfo.getName())||StringUtils.isNotBlank(userInfo.getPassword())){
            requestResult.setSuccess(false);
            requestResult.setMsg("用户名和密码不能为空！！！");
            logger.warn("用户注册失败，用户名和密码为空！！！");
            return requestResult;
        }
        // 判断用户名是否重复
        if(userDao.duplicateName(userInfo.getName())){
            requestResult.setSuccess(false);
            requestResult.setMsg("用户名重复！！！");
            logger.warn("用户注册失败，用户名重复："+userInfo.getName());
            return requestResult;
        }

        // 设置新用户的id
        userInfo.setUid(UUID.randomUUID().toString());
        if(userDao.register(userInfo)){
            requestResult.setSuccess(true);
            requestResult.setMsg("用户注册成功");
            logger.info(String.format("用户%s注册成功",userInfo.getName()));
            // 注册的时候先入库，然后通过kafka发送验证码到用户phone或email
            //TODO:发送注册码，或返回激活地址（需要确定激活的方式）
            return requestResult;
        }else{
            requestResult.setSuccess(false);
            requestResult.setMsg("用户注册失败");
            return requestResult;
        }
    }

    @Override
    public RequestResult login(UserInfo userInfo) {
        RequestResult requestResult = new RequestResult();
        UserInfo user = userDao.login(userInfo.getName(), userInfo.getPassword());
        if(user==null&&StringUtils.isNotBlank(user.getUid())){
            requestResult.setSuccess(false);
            String msg = "登录失败，用户不存在！！！";
            requestResult.setMsg(msg);
            logger.warn(msg);
            return requestResult;
        }else {
            // 如果用户已经激活就生成token返回
            // 用户未激活，就跳转到激活流程
            if(user.isActive()){
//                Map<String,String>
//                jwtUtil.generateToken()
            }else{
                // TODO:跳转到激活流程
            }
        }
        return null;
    }

    @Override
    public RequestResult update(UserInfo userInfo) {
        return null;
    }

    @Override
    public RequestResult logout() {
        return null;
    }
}
