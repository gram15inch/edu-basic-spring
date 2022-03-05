package com.example.test2.service;

import com.example.test2.dao.UserDao;
import com.example.test2.domain.Level;
import com.example.test2.domain.User;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class UserService {
    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) { this.dataSource = dataSource; }
    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }
    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);
        try{
            List<User> users = userDao.getAll();
            for(User user : users)
                if(canUpgradeLevel(user))
                    upgradeLevel(user);
            c.commit();
        }catch (Exception e){
            c.rollback();
            throw e;
        }finally {
            DataSourceUtils.releaseConnection(c,dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    protected void upgradeLevel(User user) {
        this.userLevelUpgradePolicy.upgradeLevel(user);
    }

    private boolean canUpgradeLevel(User user) {
       return this.userLevelUpgradePolicy.canUpgradeLevel(user);
    }


    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
