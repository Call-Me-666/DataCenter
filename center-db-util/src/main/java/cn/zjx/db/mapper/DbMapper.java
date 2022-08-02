package cn.zjx.db.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-08-01 16:01:45
 * @Version: 1.0
 **/
@Mapper
public interface DbMapper {

    @Insert("<script>" +
            "create database if not exists ${dbName} " +
            "default character set ${character} default collate ${collate}" +
            "</script>")
    boolean createDb(String dbName,String character,String collate);

    @Delete("<script>" +
            "drop database if exists ${dbName}" +
            "</script>")
    boolean dropDb(String dbName);

    @Select("<script>" +
            "select count(SCHEMA_NAME) from information_schema.SCHEMATA where SCHEMA_NAME = #{dbName}" +
            "</script>")
    boolean isExistsDb(String dbName);
}
