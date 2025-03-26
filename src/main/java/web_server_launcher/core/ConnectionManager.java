package web_server_launcher.core;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionManager {
    public static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);
    private static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:~/jwp-basic;AUTO_SERVER=TRUE";
    public static final String DB_USERNAME = "sa";
    public static final String DB_PW = "";

    public static DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(DB_DRIVER);
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USERNAME);
        ds.setPassword(DB_PW);
        return ds;
    }

    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
