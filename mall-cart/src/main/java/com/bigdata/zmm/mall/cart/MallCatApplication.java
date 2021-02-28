package com.bigdata.zmm.mall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2021-02-24 11:33:14
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MallCatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCatApplication.class, args);
    }

}
