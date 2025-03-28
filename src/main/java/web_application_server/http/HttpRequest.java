package web_application_server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.session.HttpCookie;
import web_application_server.session.HttpSession;
import web_application_server.session.HttpSessions;
import web_application_server.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private RequestLine requestLine;
    private HttpHeaders headers;
    private RequestParams requestParams = new RequestParams();
    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            requestLine = new RequestLine(createRequestLine(br));
            requestParams.addQueryString(requestLine.getQueryString());
            headers = processHeader(br);
            requestParams.addBody(IOUtils.readData(br, headers.getContentLength()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String createRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) {
            throw new IllegalStateException();
        }
        return line;
    }

    private HttpHeaders processHeader(BufferedReader br) throws IOException{
        HttpHeaders headers = new HttpHeaders();
        String line;
        while (!(line = br.readLine()).equals("")) {
            headers.add(line);
        }
        return headers;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public HttpCookie getCookies() {
        return new HttpCookie(getHeader("Cookie"));
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookies().getCookie("JSESSIONID"));
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.getHeader(name);
    }

    public String getParameter(String name) {
        return requestParams.getParameter(name);
    }
}
