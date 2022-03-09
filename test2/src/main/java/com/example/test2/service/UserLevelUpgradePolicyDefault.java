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

    // javaMail 생략 380p - 400p
    private void sendUpgradeEMail(User user){
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.ksug.org");
        Session s = Session.getInstance(props,null);

        MimeMessage message = new MimeMessage(s);
        try{
            message.setFrom(new InternetAddress("useradmin@ksug.org"));
            message.addRecipients(Message.RecipientType.TO,
                    String.valueOf(new InternetAddress(user.getEmail())));
            message.setSubject("Upgrade 안내");
            message.setText("사용자님의 등급이 " + user.getLevel().name()+
                    "로 업그레이드 되었습니다.");
            Transport.send(message);
        }catch (AddressException e){throw new RuntimeException(e);}
        catch (MessagingException e){throw new RuntimeException(e);}
        //catch (UnsupportedEncodingException e){throw new RuntimeException(e);} 오류
}
}
