package com.example.test2.service;

import com.example.test2.dao.UserDao;
import com.example.test2.domain.Level;
import com.example.test2.domain.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class UserLevelUpgradePolicyDefault implements UserLevelUpgradePolicy{

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch(currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return (false);
            default: throw new IllegalArgumentException("Unknown Level : "+ currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }


}
