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

    @Value("${datasource.dbName}")
    String dbName;

    @Autowired
    UserMapper userMapper;

    public boolean register(UserInfo userInfo){
        return userMapper.insert(getTableName(),userInfo);
    }

    public UserInfo login(String name,String password){
        UserInfo userInfo = userMapper.login(getTableName(),name,password);
        return userInfo;
    }

    public boolean duplicateName(String name,String uid){
        return userMapper.duplicateName(getTableName(),name,uid);
    }

    public boolean unsubscribe(String uid){
        return userMapper.delete(getTableName(),uid);
    }

    public boolean update(UserInfo userInfo){
        return userMapper.update(getTableName(),userInfo);
    }

    public boolean active(String uid){
        return userMapper.active(getTableName(),uid);
    }

    private String getTableName(){
        return dbName+".t_user";
    }
}
