package com.campus.safety;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.safety.module.*.mapper")
public class CampusSafetyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSafetyApplication.class, args);
        System.out.println("====================================");
        System.out.println("  校园安全管理系统启动成功!");
        System.out.println("  接口文档: http://localhost:8088/api/doc.html");
        System.out.println("====================================");
    }
}
