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
            responseResource(body, contentType);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String url) { // 다른 URL로 리다이렉트(302)
        // redirect 방식
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(byte[] body, String contentType) throws IOException {
        addHeader("Content-Type: ", contentType);
        responseBody(dos, body);
    }

    private void responseBody(OutputStream out, byte[] body) {
        DataOutputStream dos = new DataOutputStream(out);
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
