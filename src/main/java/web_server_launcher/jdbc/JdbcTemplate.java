package web_server_launcher.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import web_server_launcher.core.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    public static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);
    private static JdbcTemplate jdbcTemplate;

    private JdbcTemplate() {}
    public static JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate();
        }
        return jdbcTemplate;
    }

    public void update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pss.setParameters(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void update(String sql, Object... parameters) {
        update(sql, createPreparedStatementSetter(parameters));
    }

    public long update(PreparedStatementCreator psc) {
        long key = 0L;
        try (Connection conn = ConnectionManager.getConnection()) {
            PreparedStatement ps = psc.createPreparedStatement(conn);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
            rs.close();
            return key;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> T queryForObject(String sql, RowMapper<T> rm, PreparedStatementSetter pss) {
        List<T> list = query(sql, rm, pss);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public <T> T queryForObject(String sql, RowMapper<T> rm, Object... parameters) {
        return queryForObject(sql, rm, createPreparedStatementSetter(parameters));
    }

    public <T> List<T> query(String sql, RowMapper<T> rm, PreparedStatementSetter pss) throws DataAccessException {
        ResultSet rs = null;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pss.setParameters(pstmt);
            rs = pstmt.executeQuery();

            List<T> list = new ArrayList<T>();
            while (rs.next()) {
                list.add(rm.mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rm, Object... parameters) {
        return query(sql, rm, createPreparedStatementSetter(parameters));
    }

    public <T> List<T> query(String sql, RowMapper<T> rm, Long questionId) {
        return query(sql, rm, createPreparedStatementSetter(questionId));
    }

    private PreparedStatementSetter createPreparedStatementSetter(Object... parameters) {
        return new PreparedStatementSetter() {
            @Override
            public void setParameters(PreparedStatement pstmt) throws SQLException {
                for (int i = 0; i < parameters.length; i++) {
                    pstmt.setObject(i + 1, parameters[i]);
                }
            }
        };
    }

    public void delete(String sql, PreparedStatementSetter pss) throws DataAccessException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pss.setParameters(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void delete(String sql, Object... parameters) {
        delete(sql, createPreparedStatementSetter(parameters));
    }
}
