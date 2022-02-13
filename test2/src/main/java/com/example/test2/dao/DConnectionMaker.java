package com.example.test2.dao;

import com.example.test2.DBinfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DConnectionMaker implements ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException {

        Connection c = DriverManager.getConnection(
                DBinfo.URL,
                DBinfo.USERNAME,
                DBinfo.PASSWORD);

        return c;
    }
}
