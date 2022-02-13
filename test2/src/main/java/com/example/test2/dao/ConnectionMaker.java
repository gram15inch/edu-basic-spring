package com.example.test2.dao;

import java.sql.Connection;
import java.sql.SQLException;

interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
