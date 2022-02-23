package com.example.test2.dao;




import com.example.test2.DBinfo;
import com.example.test2.domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class UserDao {

    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    
    public void add(final User user) throws SQLException {
        jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)"
                ,user.getId()
                ,user.getName()
                ,user.getPassword());
    }

    public User get(String id) throws  SQLException {

        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                });


    }
    public User getBefore(String id) throws  SQLException {


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
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() throws SQLException {
        return this.jdbcTemplate.queryForObject("select count(*) from users",Integer.class);

    }

    public int getCountBefore() throws SQLException{

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

    public List<User> getAll(){
        return jdbcTemplate.query("select * from users order by id",
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                });
        //263p 까지 복습
    }
}


