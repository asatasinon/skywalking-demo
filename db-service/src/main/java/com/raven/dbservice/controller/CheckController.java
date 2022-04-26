package com.raven.dbservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: raven
 * @date: 2022/4/6
 * @description:
 */
@RestController
public class CheckController {

    private static Logger logger = LogManager.getLogger("CheckController");

    @GetMapping("check")
    public String check(@RequestParam String userName) {

        logger.info("check " + userName);


        if(!"raven".equals(userName)){
            try {
                throw new Exception("test error");
            } catch (Exception e) {
               logger.error("CheckController.check failed, cause by: {}",e);
                return  "500," + userName + "  not exists.";
            }
        }

        return  userName + " exists.";

    }
}
