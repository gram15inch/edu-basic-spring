package com.example.test2.service;

import com.example.test2.dao.UserDao;
import com.example.test2.domain.Level;
import com.example.test2.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;


public class UserServiceImpl implements UserService {
    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;
    private MailSender mailSender;

    public void setUserDao(UserDao userDao){ this.userDao = userDao; }
    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }
    public void setMailSender(MailSender mailSender) { this.mailSender = mailSender; }

    public void upgradeLevels() {
            List<User> users = userDao.getAll();
            for(User user : users)
                if(canUpgradeLevel(user))
                    upgradeLevel(user);
    }

    protected void upgradeLevel(User user) {
        this.userLevelUpgradePolicy.upgradeLevel(user);
        this.sendUpgradeEMail(user);
    }

    private boolean canUpgradeLevel(User user) {
       return this.userLevelUpgradePolicy.canUpgradeLevel(user);
    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    // javaMail 다시 시작
    private void sendUpgradeEMail(User user){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        mailSender.send(mailMessage);
    }

    private void BeforeSendUpgradeEMail(User user){
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
