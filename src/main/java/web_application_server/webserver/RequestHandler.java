package web_application_server.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;
import web_application_server.model.User;
import web_application_server.util.HttpRequestUtils;
import web_application_server.util.IOUtils;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

public class RequestHandler extends Thread {
    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        log.debug("New Client Connected. Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String path = getDefaultPath(request.getPath());
            log.info("path: " + path);

            if ("/user/create".equals(path)) {
                User user = new User(request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("eamil"));
                log.debug("user: " + user);
                DataBase.addUser(user);
                response.sendRedirect("/index.html");
            } else if ("/user/login".equals(path)) {
                User user = DataBase.findUserById(request.getParameter("userId"));
                if (user == null || !(user.login(request.getParameter("password")))) {
                    response.sendRedirect("/user/login_failed.html");
                } else {
                    if (!(request.getHeader("Cookie").contains("logined=true"))) {
                        response.addHeader("Set-Cookie", "logined=true; Path=/");
                    }
                    response.sendRedirect("/index.html");
                }
            } else if ("/user/list".equals(path)) {
                if (request.getParameter("userId") != null && isLogin(request.getHeader("Cookie"))) {
                    Collection<User> users = DataBase.findAll();
                    StringBuilder sb = userStringBuilder(users);
                    response.forwardBody(sb.toString());
                } else {
                    response.sendRedirect("/user/login.html");
                }
            } else {
                response.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }

    private StringBuilder userStringBuilder(Collection<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table borders='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb;
    }

    public boolean isLogin(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    public User signUp(String body) {
        Map<String, String> data = getBodyParam(body);
        User user = new User(data.get("userId"), data.get("password"), data.get("name"), data.get("email"));
        return user;
    }

    public Map<String, String> getBodyParam(String body) {
        Map<String, String> data = HttpRequestUtils.parseQueryString(body);
        return data;
    }

    public String getUrl(String line) {
        String[] tokens = line.split(" ");
        int index = tokens[1].indexOf("?");
        if (index == -1) {
            return tokens[1];
        }
        String token = tokens[1].substring(0, index);
        return token;
    }
}
