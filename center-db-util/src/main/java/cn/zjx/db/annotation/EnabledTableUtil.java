package cn.zjx.db.annotation;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import(EnabledTableUtilConfig.class)
@ComponentScan(basePackages = "cn.zjx.db")
public @interface EnabledTableUtil {
}
