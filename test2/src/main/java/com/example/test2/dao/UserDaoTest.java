package com.example.test2.dao;


import com.example.test2.domain.User;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
       // ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        //ApplicationContext context = new ClassPathXmlApplicationContext("daoContext.xml",UserDao.class);
        //ApplicationContext context = new ClassPathXmlApplicationContext();
        UserDao dao = context.getBean("userDao",UserDao.class);

        // user 생성
        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        // user 추가
        dao.add(user);

        // dao 실행
        System.out.println(user.getId());
        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
    }
}
