package cn.zjx.db.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Program: ApplicationPlatform
 * @Author: zjx
 * @Create: 2022-07-01 15:23:09
 * @Version: 1.0
 **/
@Mapper
public interface TableMapper {

    /**
     * 创建表
     * @param tableName 表名
     * @param tableStruct 表结构
     * @return
     */
    @Insert("<script>" +
            "create table if not exists ${dbName}.${tableName} " +
            "${tableStruct}" +
            "</script>")
    boolean createTable(String tableName,String dbName,String tableStruct);

    /**
     * 查询表是否已经存在
     * @author zjx
     * @create 2022/7/3 17:02
     * @param tableName 表名
     * @param dbName 数据库名
     * @return boolean
     */
    @Select("<script>" +
            "select count(*) from information_schema.tables " +
            "where table_name = #{tableName} and table_schema = #{dbName}" +
            "</script>")
    boolean isExistsTable(String tableName,String dbName);

    @Delete("<script>" +
            "drop table if exists ${dbName}.${tableName}" +
            "</script>")
    boolean deleteTable(String tableName,String dbName);

    /**
     * 获取指定数据库中的所有的表名
     * @param dbName
     * @return
     */
    @Select("<script>" +
            "select table_name form information_schema.tables " +
            "where table_schema = #{dbName}" +
            "</script>")
    List<String> getTables(String dbName);
}
