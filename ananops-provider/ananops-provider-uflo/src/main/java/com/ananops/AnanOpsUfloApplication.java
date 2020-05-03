package com.ananops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableHystrix
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@ImportResource(locations = {"classpath:uflo-console-context.xml"})
@ComponentScan(basePackages = "com.ananops")
@EnableJpaRepositories(basePackages = "com.ananops")
@EntityScan({
        "com.bstek.uflo.model"
        ,"com.ananops"
})
public class AnanOpsUfloApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnanOpsUfloApplication.class, args);
    }
}
