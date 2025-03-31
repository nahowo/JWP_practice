package web_server_launcher.dao;

import web_application_server.model.User;
import web_server_launcher.jdbc.JdbcTemplate;
import web_server_launcher.jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private JdbcTemplate jdbcTemplate;
    public void insert(User user) {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
    }

    public void update(User user) {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "UPDATE USERS SET password = ?, name = ?, email = ? WHERE userId = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getName(), user.getEmail(), user.getUserId());
    }

    public User findByUserId(String userId) {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "SELECT userId, password, name, email FROM USERS WHERE userId = ?";
        RowMapper<User> rm = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs) throws SQLException {
                return new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"), rs.getString("email"));
            }
        };
        return jdbcTemplate.queryForObject(sql, rm, userId);
    }

    public List<User> findAll() {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "SELECT userId, password, name, email FROM USERS";
        RowMapper<User> rowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs) throws SQLException {
                return new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"), rs.getString("email"));
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
}
