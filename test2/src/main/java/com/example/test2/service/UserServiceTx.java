package com.example.test2.service;

import com.example.test2.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserServiceTx implements UserService{
    UserService userService;
    PlatformTransactionManager transactionManager;


    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public void add(User user) {

        userService.add(user);
    }

    @Override
    public void upgradeLevels() { TransactionStatus status =
            this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            userService.upgradeLevels();
            this.transactionManager.commit(status);
        }catch (Exception e){
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    // 임시

    @Override
    public User get(String id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void update(User user) {

    }
}
