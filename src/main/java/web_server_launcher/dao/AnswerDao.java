package web_server_launcher.dao;

import web_application_server.model.Answer;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnswerDao {
    public Answer findAllById(Long questionId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE questionId = ?";
        RowMapper<Answer> rm = new RowMapper<Answer>() {
            @Override
            public Answer mapRow(ResultSet rs) throws SQLException {
                return new Answer(Long.parseLong(rs.getString("answerId")), rs.getString("writer"), rs.getString("contents"), Date.valueOf(rs.getString("createdDate")), Long.parseLong(rs.getString("questionId")));
            }
        };
        return jdbcTemplate.queryForObject(sql, rm, questionId);
    }
}
