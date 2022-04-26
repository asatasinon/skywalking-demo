package com.raven.secondnode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecondNodeApplication {
    private static Logger logger = LogManager.getLogger("SecondNodeApplication");

    public static void main(String[] args) {
        SpringApplication.run(SecondNodeApplication.class, args);
        logger.info(">>>>>>>second start");
    }

}
