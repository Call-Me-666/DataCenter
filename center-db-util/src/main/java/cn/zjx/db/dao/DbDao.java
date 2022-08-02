package cn.zjx.db.dao;

import cn.zjx.db.mapper.DbMapper;
import cn.zjx.db.mapper.TableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-08-01 16:20:37
 * @Version: 1.0
 **/
@Component
public class DbDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DbMapper dbMapper;

}
