package com.raven.services;

/**
 * @author: raven
 * @date: 2022/4/20
 * @description:
 */
public interface UserService {
    String getUserInfo(String userName);

    boolean checkUser(String userName);
}
