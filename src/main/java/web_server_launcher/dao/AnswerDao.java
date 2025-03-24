package web_server_launcher.dao;

import web_application_server.model.Answer;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AnswerDao {
    public List<Answer> findAllById(Long questionId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE questionId = ? ORDER BY createdDate DESC";
        RowMapper<Answer> rm = new RowMapper<Answer>() {
            @Override
            public Answer mapRow(ResultSet rs) throws SQLException {
                return new Answer(rs.getLong("answerId"), rs.getString("writer"), rs.getString("contents"), rs.getTimestamp("createdDate"), rs.getLong("questionId"));
            }
        };
        return jdbcTemplate.query(sql, rm, questionId);
    }
}
