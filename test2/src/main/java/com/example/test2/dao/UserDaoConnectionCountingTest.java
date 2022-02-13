package com.example.test2.dao;

import com.example.test2.domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao userDao = context.getBean("userDao",UserDao.class);
        // user 생성
        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");
        userDao.add(user);


        user.setId("whiteship2");
        user.setName("백기선2");
        user.setPassword("married2");
        userDao.add(user);
        CountingConnectionMaker ccm = context.getBean("connectionMaker",CountingConnectionMaker.class);
       System.out.println("Connection counter :"+ccm.getCounter());

    }

}
