package com.raven.dbservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DbServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbServiceApplication.class, args);

//        -javaagent:/Users/raven/Downloads/apache-skywalking-apm-bin-es7/skywalking-agent.jar=agent.service_name=db_node
    }

}
