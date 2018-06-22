package com.snail.dao.impl;

import com.snail.dao.UserDao;
import com.snail.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class UserDaoImpl implements UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public User findByName(String userName) {
        String sql = "select pswd as password,email as username from u_user where email = ?";
        List<User> list = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        });
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    public List<String> getRolesByUserName(String userName) {
        String sql = "select name from u_role r where 1=1 and exists(select ur.rid from u_user_role ur where 1=1 and r.id = ur.rid and exists(select u.id from u_user u where u.id = ur.uid and u.email=?))";
        return jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>() {
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("name");
            }
        });

    }

    public List<String> getPermissionsByRoleName(List<String> roles) {
        String coditionIn = "";
        for(String roleName : roles){
            coditionIn += "'"+roleName+"',";
        }
        if(!"".equals(coditionIn)){
            coditionIn = coditionIn.substring(0,coditionIn.length()-1);
        }
        String sql = "select p.permissioncode from u_permission p where 1=1 and p.permissioncode is not null and exists(select up.pid from u_role_permission up where 1=1 and p.id = up.pid and exists(select r.id from u_role r where 1=1 and r.id = up.rid and r.name in("+coditionIn+")))";
        return jdbcTemplate.query(sql, new RowMapper<String>() {
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("permissioncode");
            }
        });
    }
}
