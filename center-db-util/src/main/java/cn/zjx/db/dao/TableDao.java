package cn.zjx.db.dao;

import cn.zjx.db.mapper.TableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Program: ApplicationPlatform
 * @Author: zjx
 * @Create: 2022-07-01 15:27:57
 * @Version: 1.0
 **/
@Component
public class TableDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TableMapper tableMapper;

    /**
     * 数据库名称
     */
    @Value("${datasource.dbName}")
    String dbName;

    /**
     * 创建表
     * @param tableName
     * @param tableStruct
     * @return
     */
    public void createTable(String tableName,String tableStruct){
        if(dbName==null||dbName.isBlank())
        if(!tableMapper.isExistsTable(tableName,dbName)){
            tableMapper.createTable(tableName,dbName,tableStruct);
            if(tableMapper.isExistsTable(tableName,dbName)){
                logger.info(String.format("---%s表创建成功---",tableName));
            }else{
                logger.info(String.format("---%s表创建失败---",tableName));
            }
        }
    }
}
