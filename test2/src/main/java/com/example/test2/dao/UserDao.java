package com.example.test2.dao;




import com.example.test2.DBinfo;
import com.example.test2.domain.User;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDao {

    private ConnectionMaker connectionMaker ;
    public UserDao(ConnectionMaker connectionMaker){
        this.connectionMaker  = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c
                .prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }


}

interface ConnectionMaker{
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}


class DConnectionMaker implements  ConnectionMaker{
    public Connection makeConnection() throws ClassNotFoundException, SQLException{

        Connection c = DriverManager.getConnection(
                DBinfo.URL,
                DBinfo.USERNAME,
                DBinfo.PASSWORD);

        return c;
    }
}

class CountingConnectionMaker implements ConnectionMaker{
    int counter = 0;
    private  ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker connectionMaker) {
        this.realConnectionMaker = connectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.makeConnection();
    }

    public int getCounter(){
        return this.counter;
    }
}