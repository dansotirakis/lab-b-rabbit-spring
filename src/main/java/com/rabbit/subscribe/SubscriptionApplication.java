package com.rabbit.subscribe;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class SubscriptionApplication {
    @ApiOperation(value = "RabbitSubscription")
    public static void main(String[] args) {
        SpringApplication.run(SubscriptionApplication.class, args);
    }

}
