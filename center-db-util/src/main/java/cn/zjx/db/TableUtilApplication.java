package cn.zjx.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description:
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-08-01 15:30:29
 * @Version: 1.0
 **/
@SpringBootApplication
@MapperScan("cn.zjx.db.mapper")
public class TableUtilApplication {
    public static void main(String[] args) {
        SpringApplication.run(TableUtilApplication.class,args);
    }
}
