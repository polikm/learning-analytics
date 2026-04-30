package com.example.learning.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 学情数据统计分析平台 - 启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example.learning")
public class LearningAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningAdminApplication.class, args);
    }

}
