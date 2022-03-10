package com.example.test2.service;

import com.example.test2.dao.UserDao;
import com.example.test2.domain.Level;
import com.example.test2.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import static com.example.test2.service.UserLevelUpgradePolicyDefault.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.test2.service.UserLevelUpgradePolicyDefault.MIN_RECCOMEND_FOR_GOLD;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    DataSource dataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    UserDao userDao;

    List<User> users;
    @Before
    public void setUp(){
        //userDao = userService.userDao;
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1,0,"bumjin@email.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0,"joytouch@email.com"),
                new User("erwins", "신승한", "P3  ", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1,"erwins@email.com"),
                new User("madnite1", "이상호", "P4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD,"madnite1@email.com"),
                new User("green", "오민규", "P5", Level.GOLD, 100, 100,"green@email.com")
        );
    }

    @Test
    public void upgradeLevels() throws Exception{
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }else{
            assertThat(userUpdate.getLevel(),is(user.getLevel()));
        }

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

    // 테스트용 확장 클래스
    static class TestUserService extends UserServiceImpl {
        private String id;
        private TestUserService(String id){ this.id = id;}

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }
    // 테스트용 예외
    static class TestUserServiceException extends RuntimeException{}

    @Test
    public void upgradeAllOrNothing(){
        UserLevelUpgradePolicyDefault p = new UserLevelUpgradePolicyDefault();
        p.setUserDao(this.userDao);

        UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserLevelUpgradePolicy(p);
        testUserService.setUserDao(this.userDao); // add 메소드에서 필요

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try{
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }catch (TestUserServiceException e){}

        checkLevelUpgraded(users.get(1),false);

    }
}

