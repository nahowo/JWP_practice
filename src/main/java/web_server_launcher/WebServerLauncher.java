package web_server_launcher;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WebServerLauncher {
    private static final Logger log = LoggerFactory.getLogger(WebServerLauncher.class);
    public static void main(String[] args) throws Exception{
        String webappDirLocation = "webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.addWebapp("",
                new File(webappDirLocation).getAbsolutePath());
        log.info("configuring app with baseDir: " + new File("./" + webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getConnector();
        tomcat.getServer().await();
    }
}
