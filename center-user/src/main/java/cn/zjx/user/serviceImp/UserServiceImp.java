package cn.zjx.user.serviceImp;

import cn.zjx.common.jwt.util.JwtUtil;
import cn.zjx.common.pojo.RequestResult;
import cn.zjx.user.dao.UserDao;
import cn.zjx.user.pojo.UserInfo;
import cn.zjx.user.service.UserService;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 10:52:25
 * @Version: 1.0
 **/
@Service
public class UserServiceImp implements UserService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserDao userDao;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JavaMailSender javaMailSender;  // java邮件发送对象
    @Value("${spring.mail.username}")
    String myMail;

    String localUrl;    // 本地的访问地址

    /**
     * 用户注册
     * @param userInfo 用户信息
     * @return
     */
    @Override
    public RequestResult register(UserInfo userInfo) {
        RequestResult requestResult = new RequestResult();
        // 判断用户名、密码、邮箱
        if(StringUtils.isBlank(userInfo.getName())||
                StringUtils.isBlank(userInfo.getPassword())||
                StringUtils.isBlank(userInfo.getEmail())){
            requestResult.setSuccess(false);
            requestResult.setMsg("用户名、密码、邮箱不能为空！！！");
            logger.warn("用户注册失败，用户名、密码、邮箱为空！！！");
            return requestResult;
        }
        // 判断用户名是否重复
        if(userDao.duplicateName(userInfo.getName(),null)){
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
            // 发送注册码，或返回激活地址（需要确定激活的方式）
            sendActiveEmail(userInfo);
            return requestResult;
        }else{
            requestResult.setSuccess(false);
            requestResult.setMsg("用户注册失败");
            return requestResult;
        }
    }

    /**
     * 登录
     * @param userInfo
     * @return
     */
    @Override
    public RequestResult login(UserInfo userInfo) {
        RequestResult requestResult = new RequestResult();
        UserInfo user = userDao.login(userInfo.getName(), userInfo.getPassword());
        if(user==null||StringUtils.isBlank(user.getUid())){
            requestResult.setSuccess(false);
            String msg = "登录失败，用户不存在！！！";
            requestResult.setMsg(msg);
            logger.warn(msg);
        }else {
            // 如果用户已经激活就生成token返回
            // 用户未激活，就跳转到激活流程
            if(user.isActive()){
                Map<String,String> claimsMap = new HashMap<>();
                claimsMap.put("uid",user.getUid());
                claimsMap.put("name",user.getName());
                claimsMap.put("password",user.getPassword());
                String token = jwtUtil.generateToken(claimsMap,JwtUtil.MINUTE,30);
                requestResult.setSuccess(true);
                requestResult.setMsg("登录成功");
                Map<String,String> resultMap = new HashMap<>();
                resultMap.put("token",token);
                requestResult.setResult(resultMap);
            }else{
                requestResult.setSuccess(false);
                requestResult.setMsg("登录失败，用户未激活，激活邮件已经发送到邮箱，请先到邮箱中激活");
                // 重新发送激活邮件
                sendActiveEmail(user);
            }
        }
        return requestResult;
    }

    /**
     * 修改用户信息
     * @param userInfo
     * @return
     */
    @Override
    public RequestResult update(UserInfo userInfo) {
        RequestResult requestResult = new RequestResult();
        // 判断用户名、密码、邮箱
        if(StringUtils.isBlank(userInfo.getName())||
                StringUtils.isBlank(userInfo.getPassword())||
                StringUtils.isBlank(userInfo.getEmail())){
            requestResult.setSuccess(false);
            requestResult.setMsg("用户名、密码、邮箱不能为空！！！");
            logger.warn("用户修改失败，用户名、密码、邮箱为空！！！");
            return requestResult;
        }

        // 判断用户名是否重复
        if(userDao.duplicateName(userInfo.getName(),userInfo.getUid())){
            requestResult.setSuccess(false);
            requestResult.setMsg("用户名重复！！！");
            logger.warn("用户修改失败，用户名重复："+userInfo.getName());
            return requestResult;
        }

        if(userDao.update(userInfo)){
            requestResult.setSuccess(true);
            requestResult.setMsg("用户修改成功！！！");
            requestResult.setResult(userInfo);
            logger.info("用户修改成功，用户名："+userInfo.getName());
        }else{
            requestResult.setSuccess(true);
            requestResult.setMsg("用户修改失败！！！");
        }
        return requestResult;
    }

    /**
     * 注销用户
     * @param userInfo
     * @return
     */
    @Override
    public RequestResult unsubscribe(UserInfo userInfo) {
        RequestResult requestResult = new RequestResult();
        if(userInfo!=null&&StringUtils.isNotBlank(userInfo.getUid())){
            boolean isSuccess = userDao.unsubscribe(userInfo.getUid());
            requestResult.setSuccess(isSuccess);
            requestResult.setMsg("用户注销成功");
        }else{
            requestResult.setSuccess(false);
            requestResult.setMsg("用户信息不全，注销失败");
        }
        return requestResult;
    }

    /**
     * 用户激活
     * @param token 用户token
     * @return
     */
    @Override
    public RequestResult active(String token) {
        UserInfo userInfo = null;
        DecodedJWT decodedJWT = jwtUtil.decodeToken(token);
        if(decodedJWT!=null){
            Map<String, Claim> claims = decodedJWT.getClaims();
            userInfo = new UserInfo();
            userInfo.setUid(claims.get("uid").asString());
            userInfo.setName(claims.get("name").asString());
            userInfo.setPassword(claims.get("password").asString());
        }
        RequestResult requestResult = new RequestResult();
        if(userInfo!=null&&StringUtils.isNotBlank(userInfo.getUid())){
            boolean isSuccess = userDao.active(userInfo.getUid());
            requestResult.setSuccess(isSuccess);
            requestResult.setMsg(isSuccess?"激活成功":"激活失败，请重新激活");
        }else{
            requestResult.setMsg("激活失败，请重新激活");
            requestResult.setSuccess(false);
        }
        return requestResult;
    }

    /**
     * 发送激活链接到邮箱
     * @param targetMail 目标邮件
     * @param text 内容
     */
    private void sendEmail(String targetMail,String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(myMail);
        message.setTo(targetMail);
        message.setSubject("激活邮件");
        message.setText(text);
        javaMailSender.send(message);
    }

    /**
     * 发送激活邮件
     * @param userInfo
     */
    private void sendActiveEmail(UserInfo userInfo){
        // TODO:改成通过kafka进行异步的发送激活邮件
        Map<String,String> claimsMap = new HashMap<>();
        claimsMap.put("uid",userInfo.getUid());
        claimsMap.put("name",userInfo.getName());
        claimsMap.put("password",userInfo.getPassword());
        // 激活的token超时时间是1分钟
        String token = jwtUtil.generateToken(claimsMap,JwtUtil.MINUTE,1);
        // 服务地址为空的情况下，需要重新获取地址
        if(StringUtils.isBlank(localUrl)){
            localUrl = getServletUrl();
        }
        sendEmail(userInfo.getEmail(), String.format("%s/user/active/%s",localUrl,token));
    }

    /**
     * 获取Servlet当前的url
     * @return
     */
    public static String getServletUrl() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String localAddr = request.getLocalAddr();
        int serverPort = request.getServerPort();
        return "http://" + localAddr + ":" + serverPort;
    }

    @Override
    public void test() {
        String test = getServletUrl();
    }
}
