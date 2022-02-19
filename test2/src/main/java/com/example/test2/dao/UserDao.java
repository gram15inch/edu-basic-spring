package com.example.test2.dao;




import com.example.test2.DBinfo;
import com.example.test2.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDao {

    private DataSource dataSource ;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt)throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {}

            if (c != null)
                try {
                    c.close();
                } catch (SQLException e) {}
        }
    }

    public void add(User user) throws SQLException {
        StatementStrategy st = new AddStatement(user);
        jdbcContextWithStatementStrategy(st);
    }

    public User get(String id) throws  SQLException {


        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try{
            c = dataSource.getConnection();
            ps = c.prepareStatement("select * from users where id = ?");
            ps.setString(1, id);

            rs = ps.executeQuery();

     /*   User user = new User();
        rs.next();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));*/

            if(rs.next()) {
                user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }

            if(user==null) throw new EmptyResultDataAccessException(1);
            return user;
        }catch (SQLException e){
            throw e;
        }finally {
            if(c!=null)
                try{ c.close();
                }catch (SQLException e){}
            if(ps!=null)
                try{ ps.close();
                }catch (SQLException e){}
            if(rs!=null)
                try{ rs.close();
                }catch (SQLException e){}
        }


    }

    public void deleteAll()throws SQLException{
        StatementStrategy st = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(st);
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }catch (SQLException e){
            throw e;
        }finally {
            if(c!=null)
                try{ c.close();
                }catch (SQLException e){}
            if(ps!=null)
                try{ ps.close();
                }catch (SQLException e){}
            if(rs!=null)
                try{ rs.close();
                }catch (SQLException e){}
        }



    }

}


