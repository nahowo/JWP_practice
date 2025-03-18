package web_application_server.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_application_server.model.User;
import web_application_server.util.HttpRequestUtils;
import web_application_server.util.IOUtils;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            int contentLength = 0;
            log.info("request line: " + line);
            String url = getUrl(line);
            boolean logined = false;

            while (!"".equals(line = br.readLine())) {
                if (line == null) {
                    return;
                }
                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }
                if (line.contains("Cookie")) {
                    logined = isLogin(line);
                }
                log.info("header: " + line);
            }
            if ("/user/create".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                User user = signUp(body);
                log.debug("user: " + user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html", null);

                DataBase.addUser(user);

            } else if ("/user/login".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> param = getBodyParam(body);
                User user = DataBase.findUserById(param.get("userId"));
                log.debug("logined user: " + user);
                if (user == null || !(user.getPassword().equals(param.get("password")))) {
                    responseResource(out, "/user/login_failed.html");
                } else {
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, "/index.html", "logined=true");
                }
            } else if ("/user/list".equals(url)) {
                if (!logined) {
                    responseResource(out, "/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                byte[] body = userStringBuilder(users);
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length, "text/html;charset=utf-8");
                responseBody(dos, body);
            } else if (url.endsWith(".css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length, "text/css");
                responseBody(dos, body);
            } else {
                responseResource(out, url);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] userStringBuilder(Collection<User> users) {
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
        return sb.toString().getBytes();
    }

    public boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    private int getContentLength(String line) {
        String[] tokens = line.split(":");
        return Integer.parseInt(tokens[1].trim());
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

    private void response200Header(DataOutputStream dos, int lenghtOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 ok \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-length: " + lenghtOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url, String cookies) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            if (cookies != null) {
                dos.writeBytes("Set-Cookie: " + cookies + " \r\n");
            }
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length, "text/html;charset=utf-8");
        responseBody(dos, body);
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
