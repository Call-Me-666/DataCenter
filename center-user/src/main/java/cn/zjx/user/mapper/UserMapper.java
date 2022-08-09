package cn.zjx.user.mapper;

import cn.zjx.user.pojo.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 11:00:14
 * @Version: 1.0
 **/
@Mapper
public interface UserMapper {
    //region insert
    @Insert("<script>" +
            "insert into ${tableName}(uid,name,password,phone,email,image,isActive) " +
            "values(#{userInfo.uid},#{userInfo.name},#{userInfo.password},#{userInfo.phone},#{userInfo.email},#{userInfo.image},#{userInfo.isActive})" +
            "</script>")
    boolean insert(String tableName,UserInfo userInfo);
    //endregion

    //region select

    @Select("<script>" +
            "select uid,name,password,phone,email,image,isActive from ${tableName} where 1=1" +
            "</script>")
    List<UserInfo> selectUsers(String tableName);

    @Select("<script>select uid,name,password,phone,email,image,isActive from ${tableName} where uid = #{uid}</script>")
    UserInfo selectByUid(String tableName,String uid);

    @Select("<script>" +
            "select uid,name,password,phone,email,image,isActive from ${tableName} where name = #{name} and password = #{password}" +
            "</script>")
    UserInfo login(String tableName,String name,String password);

    @Select("<script>" +
            "select count(uid) from ${tableName} where name =#{name} " +
            "<if test='uid!=null'>and uid!=#{uid}</if>" +
            "</script>")
    boolean duplicateName(String tableName,String name,String uid);

    //endregion

    //region update

    @Update("<script>update ${tableName} set isActive = 1 where uid =#{uid}</script>")
    boolean active(String tableName,String uid);

    @Update("<script>" +
            "update ${tableName} set " +
            "name = #{userInfo.name}," +
            "password = #{userInfo.password}," +
            "phone = #{userInfo.phone}," +
            "email = #{userInfo.email}," +
            "image = #{userInfo.image} " +
            "where uid = #{userInfo.uid}" +
            "</script>")
    boolean update(String tableName,UserInfo userInfo);

    //endregion

    //region delete

    @Delete("<script>" +
            "delete from ${tableName} where uid =${uid}" +
            "</script>")
    boolean delete(String tableName,String uid);

    //endregion
}
