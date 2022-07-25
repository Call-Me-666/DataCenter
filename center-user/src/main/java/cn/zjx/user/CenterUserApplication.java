package cn.zjx.user;

import cn.zjx.common.jwt.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CenterUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CenterUserApplication.class, args);
    }

    @Bean
    public JwtUtil getJwtUtil(){
        return new JwtUtil();
    }
}
