package web_server_launcher.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import web_application_server.model.Question;
import web_server_launcher.jdbc.JdbcTemplate;
import web_server_launcher.jdbc.RowMapper;

import java.sql.*;
import java.util.List;

public class QuestionDao {
    private static Logger log = LoggerFactory.getLogger(QuestionDao.class);
    private JdbcTemplate jdbcTemplate;
    public void insert(Question question) {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "INSERT INTO QUESTIONS (writer, title, contents, createdDate, countOfAnswer) VALUES (?, ?, ?, ?, ?)";
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, question.getWriter());
                pstmt.setString(2, question.getTitle());
                pstmt.setString(3, question.getContents());
                pstmt.setTimestamp(4, new Timestamp(question.getTimeFromCreateDate()));
                pstmt.setInt(5, question.getCountOfComment());

                return pstmt;
            }
        };
        long key = jdbcTemplate.update(psc);
        log.info("key: " + key);
    }

    public void update(String title, String contents, long questionId) {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "UPDATE QUESTIONS SET title = ?, contents = ? WHERE questionId = ?";
        jdbcTemplate.update(sql, title, contents, questionId);
    }

    public Question findById(Long questionId) {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "SELECT questionId, writer, title, contents, createdDate, countOfAnswer FROM QUESTIONS WHERE questionId = ?";
        RowMapper<Question> rm = new RowMapper<Question>() {
            @Override
            public Question mapRow(ResultSet rs) throws SQLException {
                return new Question(rs.getLong("questionId"), rs.getString("writer"), rs.getString("title"), rs.getString("contents"), rs.getTimestamp("createdDate"), rs.getInt("countOfAnswer"));
            }
        };
        return jdbcTemplate.queryForObject(sql, rm, questionId);
    }

    public List<Question> findAll() {
        jdbcTemplate = JdbcTemplate.getJdbcTemplate();
        String sql = "SELECT questionId, writer, title, contents, createdDate, countOfAnswer FROM QUESTIONS ORDER BY createdDate";
        RowMapper<Question> rm = new RowMapper<Question>() {
            @Override
            public Question mapRow(ResultSet rs) throws SQLException {
                return new Question(rs.getLong("questionId"), rs.getString("writer"), rs.getString("title"), rs.getString("contents"), rs.getTimestamp("createdDate"), rs.getInt("countOfAnswer"));
            }
        };
        return jdbcTemplate.query(sql, rm);
    }
}
