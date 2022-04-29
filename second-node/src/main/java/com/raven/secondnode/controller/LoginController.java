package com.raven.secondnode.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author: raven
 * @date: 2022/4/6
 * @description:
 */
@RestController
public class LoginController {

    private static Logger logger = LogManager.getLogger("LoginController");

    @GetMapping("login")
    public String login(@RequestParam String userName) {
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject("http://localhost:9093/check?userName=" + userName, String.class);

        logger.info(result);
        if (result.startsWith("500,")) {
            return userName + " login failed.";
        }

        String userInfo = restTemplate.getForObject("http://localhost:9093/userInfo?userName=" + userName, String.class);
        logger.info(userInfo);


        return userName + " login success.";

    }


}
