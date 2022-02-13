package com.example.test2.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
    @Bean
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

    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
