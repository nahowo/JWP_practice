package web_application_server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
    private String method;
    private String path;
    private String queryString;

    RequestLine(String requestLine) {
        log.debug("request line: " + requestLine);
        String[] tokens = requestLine.split(" ");
        this.method = tokens[0];

        String[] url = tokens[1].split("\\?");
        this.path = url[0];

        if (url.length == 2) {
            this.queryString = url[1];
        }
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getQueryString() {
        return queryString;
    }
}
