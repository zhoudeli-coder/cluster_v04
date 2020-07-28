package com.example.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan("com.example.common.entity")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement // 开启事务管理
@MapperScan(basePackages = {
        "com.example.product.service.mapper",
        "com.example.product.mapper"
})
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
