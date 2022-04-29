package com.raven.dbservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author: raven
 * @date: 2022/4/28
 * @description:
 */

@RestController
public class MenuController {

    private static Logger logger = LogManager.getLogger(MenuController.class);


    @GetMapping("menu")
    public String getMenu(@RequestParam String userName) {
        RestTemplate restTemplate = new RestTemplate();

        String userInfo = restTemplate.getForObject("http://localhost:9093/check?userName=" + userName, String.class);
        logger.info(userInfo);


        return  userName + ": getMenu success.";
    }

}
