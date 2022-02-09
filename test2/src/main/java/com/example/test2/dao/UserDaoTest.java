package com.example.test2.dao;


import com.example.test2.domain.User;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
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
