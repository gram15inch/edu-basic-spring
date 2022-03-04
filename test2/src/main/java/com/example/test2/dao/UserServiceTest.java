package com.example.test2.dao;

import com.example.test2.domain.Level;
import com.example.test2.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    UserDao userDao;
    List<User> users;
    @Before
    public void setUp(){
        userDao = userService.userDao;
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, 49,0),
                new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
                new User("erwins", "신승한", "P3", Level.SILVER, 60, 29),
                new User("madnite1", "이상호", "P4", Level.SILVER, 60, 30),
                new User("green", "오민규", "P5", Level.GOLD, 100, 100)
        );
    }

    @Test
    public void upgradeLevels(){
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWtihLevel = users.get(3);
        User userWtihtoutLevel = users.get(0);
        userWtihtoutLevel.setLevel(null);

        userService.add(userWtihLevel);
        userService.add(userWtihtoutLevel);

        assertThat(userDao.get(userWtihLevel.getId()).getLevel(),is(userWtihLevel.getLevel()));
        assertThat(userDao.get(userWtihtoutLevel.getId()).getLevel(),is(Level.BASIC));

    }
}
