package web_application_server.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.User;
import web_application_server.util.HttpRequestUtils;
import web_application_server.util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

public class RequestHandler extends Thread {
    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    private static HttpRequestUtils httpRequestUtils;
    private static IOUtils ioUtils;
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
            String url = splitUrl(line);
            log.info("url: " + url);

            while (!"".equals(line = br.readLine())) {
                if (line == null) {
                    return;
                }
                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }
                log.info("header: " + line);
            }
            if ("/user/create".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                User user = signUp(body);
                log.debug("user: " + user);
                url = "/index.html";
            }

            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private int getContentLength(String line) {
        String[] tokens = line.split(":");
        return Integer.parseInt(tokens[1].trim());
    }

    public User signUp(String body) {
        Map<String, String> data = httpRequestUtils.parseQueryString(body);
        User user = new User(data.get("userId"), data.get("password"), data.get("name"), data.get("email"));
        return user;
    }

    public String splitUrl(String line) {
        String[] tokens = line.split(" ");
        int index = tokens[1].indexOf("?");
        if (index == -1) {
            return tokens[1];
        }
        String token = tokens[1].substring(0, index);
        return token;
    }

    private void response200Header(DataOutputStream dos, int lenghtOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 ok \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-length: " + lenghtOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
