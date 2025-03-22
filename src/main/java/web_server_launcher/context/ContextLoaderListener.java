package web_server_launcher.context;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ContextLoaderListener.class);
    private static final String JDBC_URL = "jdbc:h2:~/test";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";
    private static final String SQL_FILE_PATH = "./src/main/resources/data.sql";
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Initializing Context! ");
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            Statement stmt = conn.createStatement();
            BufferedReader br = new BufferedReader(new FileReader(SQL_FILE_PATH));

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sql.append(line).append("\n");
            }

            for (String query : sql.toString().split(";")) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query.trim());
                }
            }
            log.info("Successfully initalized H2 Database!");
        } catch (SQLException | IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
