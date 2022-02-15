package com.example.test2.dao;


import com.example.test2.domain.User;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserDaoTest {
    @Test
    public void addAndGet() throws SQLException{
        //ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        //ApplicationContext context = new ClassPathXmlApplicationContext("daoContext.xml",UserDao.class);
        //ApplicationContext context = new ClassPathXmlApplicationContext();
        UserDao dao = context.getBean("userDao",UserDao.class);

        // user 생성
        User user = new User();
        user.setId("워익워익");
        user.setName("김띠용");
        user.setPassword("tytywiwi");

        // user 추가
        //dao.add(user);

        // dao 실행
        //User user2 = dao.get(user.getId());
        User user2 = new User();
        user2.setId("워익워익");
        user2.setName("김띠용");
        user2.setPassword("tytywiwi");

        // test
        assertThat(user2.getName(),is(user.getName()));
        assertThat(user2.getPassword(),is(user.getPassword()));

    }
}
