package com.example.test2.dao;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {

    public UserDao userDao(){
        ConnectionMaker connectionMaker = connectionMaker();
        UserDao userDao = new UserDao(connectionMaker());
        return userDao;
    }
    public MessageDao messageDao(){
        return new MessageDao(connectionMaker());
    }
    public AccountDao accountDao(){
        return new AccountDao(connectionMaker());
    }

    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
