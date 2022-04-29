package com.raven.first.controller;

import com.raven.services.MenuService;
import com.raven.services.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author: raven
 * @date: 2022/4/6
 * @description:
 */

@RestController
public class AuthController {

    private static Logger logger = LogManager.getLogger("AuthController");

    @DubboReference(check = false)
    private UserService userService;

    @DubboReference(check = false)
    private MenuService menuService;

    @GetMapping("/token")
    public String getToken(@RequestParam String userName) throws URISyntaxException {

        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject("http://localhost:9092/login?userName=" + userName, String.class);
        logger.info(result);


        String menuInfo = restTemplate.getForObject("http://localhost:9093/menu?userName=" + userName, String.class);
        logger.info(menuInfo);
        return result;

    }

    @GetMapping("/user")
    public String getUserInfo(@RequestParam String userName) throws URISyntaxException {

        String result = userService.getUserInfo(userName);
        String menuResult = menuService.getMenu();
        logger.info(result);
        logger.info(menuResult);
        return result + menuResult;

    }


}
