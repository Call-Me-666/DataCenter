package cn.zjx.common.pojo;

import lombok.Data;

/**
 * @Description: 请求结果
 * @Program: data-center
 * @Author: zjx
 * @Create: 2022-07-21 10:11:46
 * @Version: 1.0
 **/
@Data
public class RequestResult {
    boolean isSuccess;
    Object result;
    String msg;
}
