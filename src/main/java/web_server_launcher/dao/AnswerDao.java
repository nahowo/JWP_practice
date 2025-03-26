package web_server_launcher.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import web_application_server.model.Answer;
import web_server_launcher.jdbc.JdbcTemplate;
import web_server_launcher.jdbc.RowMapper;

import java.sql.*;
import java.util.List;

public class AnswerDao {
    public static final Logger log = LoggerFactory.getLogger(AnswerDao.class);
    public Answer insert(Answer answer) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "INSERT INTO ANSWERS (writer, contents, createdDate, questionId) VALUES (?, ?, ?, ?)";
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, answer.getWriter());
                pstmt.setString(2, answer.getContents());
                pstmt.setTimestamp(3, new Timestamp(answer.getTimeFromCreateDate()));
                pstmt.setLong(4, answer.getQuestionId());
                return pstmt;
            }
        };
        long key = jdbcTemplate.update(psc);
        return findById(key);
    }

    public Answer findById(long answerId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE answerId = ?";
        RowMapper<Answer> rm = new RowMapper<Answer>() {
            @Override
            public Answer mapRow(ResultSet rs) throws SQLException {
                return new Answer(rs.getLong("answerId"), rs.getString("writer"), rs.getString("contents"), rs.getTimestamp("createdDate"), rs.getLong("questionId"));
            }

        };
        return jdbcTemplate.queryForObject(sql, rm, answerId);
    }

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
