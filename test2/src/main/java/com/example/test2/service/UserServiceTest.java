package com.example.test2.service;

import com.example.test2.dao.UserDao;
import com.example.test2.domain.Level;
import com.example.test2.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.example.test2.service.UserLevelUpgradePolicyDefault.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.test2.service.UserLevelUpgradePolicyDefault.MIN_RECCOMEND_FOR_GOLD;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    // @Autowired DataSource dataSource;
    //@Autowired UserServiceImpl userServiceImpl;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired UserService userService;
    @Autowired UserDao userDao;
    @Autowired MailSender mailSender;
    @Autowired ApplicationContext context;


    List<User> users;
    @Before public void setUp(){
        //userDao = userService.userDao;
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1,0,"bumjin@email.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0,"joytouch@email.com"),
                new User("erwins", "신승한", "P3  ", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1,"erwins@email.com"),
                new User("madnite1", "이상호", "P4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD,"madnite1@email.com"),
                new User("green", "오민규", "P5", Level.GOLD, 100, 100,"green@email.com")
        );
    }

    @Test public void mockUpgradeLevels() throws Exception {

        // mock 주입
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserLevelUpgradePolicy policy = new UserLevelUpgradePolicyDefault();
        userServiceImpl.setUserLevelUpgradePolicy(policy);

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        // 검증
        // userDao
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(),is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(),is(Level.GOLD));

        // mailSender
        ArgumentCaptor<SimpleMailMessage> mailMessageArg =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));


    }

    @Test public void upgradeLevels() throws Exception{
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserLevelUpgradePolicy policy = new UserLevelUpgradePolicyDefault();
        userServiceImpl.setUserLevelUpgradePolicy(policy);

        MockUserDao mockUserDao = new MockUserDao((this.users));
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);


        userServiceImpl.upgradeLevels();


       List<User> updated = mockUserDao.getUpdated();
       assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0),"joytouch",Level.SILVER);
        checkUserAndLevel(updated.get(1),"madnite1",Level.GOLD);


        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    // 헬퍼 메소드
    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }else{
            assertThat(userUpdate.getLevel(),is(user.getLevel()));
        }

    }
    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        assertThat(updated.getId(),is(expectedId));
        assertThat(updated.getLevel(),is(expectedLevel));
    }

    @Test public void add(){
        userDao.deleteAll();

        User userWtihLevel = users.get(3);
        User userWtihtoutLevel = users.get(0);
        userWtihtoutLevel.setLevel(null);

        userService.add(userWtihLevel);
        userService.add(userWtihtoutLevel);

        assertThat(userDao.get(userWtihLevel.getId()).getLevel(),is(userWtihLevel.getLevel()));
        assertThat(userDao.get(userWtihtoutLevel.getId()).getLevel(),is(Level.BASIC));

    }

    // 테스트용 클래스
    static class TestUserService extends UserServiceImpl {
        private String id;
        private TestUserService(String id){ this.id = id;}

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() { return requests; }

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage[] mailMessage) throws MailException { }
    }
    static class MockUserDao implements UserDao{
        private List<User> users;
        private List<User> updated = new ArrayList<>();
        private MockUserDao(List<User> users){
            this.users = users;
        }

        public List<User> getUpdated(){
            return this.updated;
        }

        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }


        // 사용되지 않는 메서드
        public void add(User user) {
            throw new UnsupportedOperationException();
        }
        public User get(String id) {
            throw new UnsupportedOperationException();
        }
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }
        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }
    // 테스트용 예외
    static class TestUserServiceException extends RuntimeException{}

    // 학습 테스트
    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception{
        UserLevelUpgradePolicyDefault p = new UserLevelUpgradePolicyDefault();

        UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserLevelUpgradePolicy(p);
        testUserService.setUserDao(this.userDao); // policy 밖 add 메소드에서 필요
        testUserService.setMailSender(mailSender);


        ProxyFactoryBean txProxyFactoryBean =
                context.getBean("&userService",ProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try{
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }catch (TestUserServiceException e){}

        checkLevelUpgraded(users.get(1),false);

    }


}

