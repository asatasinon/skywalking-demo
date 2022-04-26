package com.raven.first;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FirstApplication {
    private static Logger logger = LogManager.getLogger("FirstApplication");

    public static void main(String[] args) {
        SpringApplication.run(FirstApplication.class, args);
        logger.info(">>>>>>>first start");
    }

}
