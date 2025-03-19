package web_application_server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(java.net.http.HttpResponse.class);
    private DataOutputStream dos = null;
    private Map<String, String> headers = new HashMap<String, String>();

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void forward(String url) { // 파일을 직접 읽어 응답으로 보냄
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            String contentType;
            if (url.endsWith(".css")) {
                contentType = "text/css";
            } else if (url.endsWith(".js")) {
                contentType = "application/javascript";
            } else {
                contentType = "text/html;charset=utf-8";
            }
            headers.put("Content-Type", contentType);
            headers.put("Content-Length", body.length + "");
            response200Header();
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String body) {
        byte[] contents = body.getBytes();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", body.length() + "");
        response200Header();
        responseBody(contents);
    }

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String url) { // 다른 URL로 리다이렉트(302)
        // redirect 방식
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: " + url + " \r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void processHeaders() {
        try {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headers.get(key) + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
