package com.raven.dubboprovider.service.impl;

import com.raven.services.MenuService;
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

@DubboService(cluster = ClusterRules.BROADCAST, loadbalance = LoadbalanceRules.CONSISTENT_HASH)
public class MenuServiceImpl implements MenuService {
    private static Logger logger = LogManager.getLogger("MenuServiceImpl");

    @Override
    public String getMenu() {
        String result = "getMenu-" + new Random().nextInt(10000);
        logger.info(result);
        return result;
    }

    @Override
    public String getMenuInfo(String name) {
        String result = name + "-" + new Random().nextInt(10000);
        logger.info(result);
        return result;
    }
}
