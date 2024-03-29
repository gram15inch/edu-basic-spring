package com.example.test2.dao;


import com.example.test2.DBinfo;
import com.example.test2.domain.Level;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {
    private User user1;
    private User user2;
    private User user3;

    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext context;

    @Before
    public void setUp(){
        //ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        //this.dao = context.getBean("userDao",UserDao.class);
        //DataSource dataSource = new SingleConnectionDataSource(
        //       "jdbc:mysql://localhost/testspring", DBinfo.USERNAME,DBinfo.PASSWORD,true);
        //dao.setDataSource(dataSource);
        this.user1 = new User("gyumee","박성철","tytywiwi1", Level.BASIC,1,0,"gyumee@email.com");
        this.user2 = new User("leegw700","이길원","tytywiwi2", Level.SILVER,55,10,"@email.com");
        this.user3 = new User("bumjin","박범진","tytywiwi3", Level.GOLD,100,40,"@email.com");

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
        checkSameUsers(userget1,user1);
        
        User userget2 = dao.get(user2.getId());
        checkSameUsers(userget2,user2);

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
    
    @Test
    public void getAll() throws SQLException{
        dao.deleteAll();
        List<User> users0 = dao.getAll();
        assertThat(users0.size(),is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(),is(1));
        checkSameUsers(user1,users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(),is(2));
        checkSameUsers(user1,users2.get(0));
        checkSameUsers(user2,users2.get(1));
        
        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(),is(3));
        checkSameUsers(user3,users3.get(0));
        checkSameUsers(user1,users3.get(1));
        checkSameUsers(user2,users3.get(2));


    }
    private void checkSameUsers(User user1, User user2){
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
        assertThat(user1.getEmail(), is(user2.getEmail()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }

    // @Test 중첩된 예외가 같이나옴
    public void sqlExceptionTranslate2(){
        dao.deleteAll();

        try {
            dao.add(user1);
            dao.add(user1);
        }catch (DuplicateKeyException ex){
            SQLException sqlEx = (SQLException)ex.getRootCause();
            SQLExceptionTranslator set =
                    new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            assertThat(set.translate(null,null,sqlEx),
                    is(DuplicateKeyException.class));
        }
    }

    @Test
    public void update(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSameUsers(user1, user1update);
        User user2same = dao.get(user2.getId());
        checkSameUsers(user2, user2same);
    }
}
