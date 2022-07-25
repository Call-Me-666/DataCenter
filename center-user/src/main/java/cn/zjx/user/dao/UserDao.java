package cn.zjx.user.dao;

import cn.zjx.user.mapper.UserMapper;
import cn.zjx.user.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 11:21:39
 * @Version: 1.0
 **/
@Component
public class UserDao {

    @Value("${dbName}")
    String dbName;

    @Autowired
    UserMapper userMapper;

    final String tableName = dbName+".t_user";

    public boolean register(UserInfo userInfo){
        return userMapper.insert(tableName,userInfo);
    }

    public UserInfo login(String name,String password){
        UserInfo userInfo = userMapper.login(tableName,name,password);
        return userInfo;
    }

    public boolean duplicateName(String name){
        return userMapper.duplicateName(tableName,name);
    }

    public boolean logout(String uid){
        return userMapper.delete(tableName,uid);
    }
}
