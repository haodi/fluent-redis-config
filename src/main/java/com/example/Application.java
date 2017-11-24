package com.example;

import com.example.service.HelloRedisService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        HelloRedisService redisService = (HelloRedisService) context.getBean("helloRedisService");
        redisService.hello();
    }
}
