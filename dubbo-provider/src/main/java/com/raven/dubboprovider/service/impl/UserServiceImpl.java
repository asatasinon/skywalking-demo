package com.raven.dubboprovider.service.impl;

import com.raven.services.UserService;
import org.apache.dubbo.common.constants.ClusterRules;
import org.apache.dubbo.common.constants.LoadbalanceRules;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * @author: raven
 * @date: 2022/4/21
 * @description:
 */


@DubboService(cluster = ClusterRules.BROADCAST)
public class UserServiceImpl implements UserService {

    private static Logger logger = LogManager.getLogger("UserServiceImpl");

    @Override
    public String getUserInfo(String userName) {
        String result = userName + "-" + new Random().nextInt(10000);
        logger.info(result);
        return result;
    }

    @Override
    public boolean checkUser(String userName) {
        if("raven".equals(userName)){
            return true;
        }
        return false;
    }
}
