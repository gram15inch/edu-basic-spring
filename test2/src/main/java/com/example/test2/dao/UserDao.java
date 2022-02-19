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

    private JdbcContext jdbcContext;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(dataSource);
        this.dataSource = dataSource;
    }

    
    public void add(final User user) throws SQLException {
        jdbcContext.workWithStatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps;
                        ps = c.prepareStatement(
                                "insert into users(id, name, password) values(?,?,?)");
                        ps.setString(1, user.getId());
                        ps.setString(2, user.getName());
                        ps.setString(3, user.getPassword());

                        return ps;
                    }
                }
        );

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
        this.jdbcContext.executeSql("delete from users");
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


