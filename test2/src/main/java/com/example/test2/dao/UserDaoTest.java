package com.example.test2.dao;


import com.example.test2.domain.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTest {
    private User user1;
    private User user2;
    private User user3;

    @Autowired
    private UserDao dao;

    @Autowired
    private ApplicationContext context;

    @Before
    public void setUp(){
        //ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        //this.dao = context.getBean("userDao",UserDao.class);
        this.user1 = new User("워익워익","김띠용","tytywiwi1");
        this.user2 = new User("워익워","띠용","tytywiwi2");
        this.user3 = new User("워익","용","tytywiwi3");
    }

    @Test
    public void addAndGet() throws SQLException{
        //ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        //ApplicationContext context = new ClassPathXmlApplicationContext("daoContext.xml",UserDao.class);
        //ApplicationContext context = new ClassPathXmlApplicationContext();



        // db 초기화
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        // user 추가
        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(),is(2));
        
        // get test
        User userget1 = dao.get(user1.getId());
        assertThat(userget1.getName(),is(user1.getName()));
        assertThat(userget1.getPassword(),is(user1.getPassword()));
        
        User userget2 = dao.get(user2.getId());
        assertThat(userget2.getName(),is(user2.getName()));
        assertThat(userget2.getPassword(),is(user2.getPassword()));

    }

    @Test
    public void count()throws  SQLException{


        // db 초기화
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        // user 추가후 갯수 비교
        dao.add(user1);
        assertThat(dao.getCount(),is(1));

        dao.add(user2);
        assertThat(dao.getCount(),is(2));

        dao.add(user3);
        assertThat(dao.getCount(),is(3));

       

    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException{

        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.get("unknown_id");
    }

}
