import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web_application_server.util.HttpRequestUtils;
import web_application_server.webserver.RequestHandler;
import web_application_server.model.User;

import java.net.Socket;
import java.util.Map;

public class RequestHandlerTest {
    private RequestHandler requestHandler;
    private static HttpRequestUtils httpRequestUtils;
    @BeforeEach
    public void setUp() {
        requestHandler = new RequestHandler(new Socket());
    }
    @Test
    public void getUrlTest() {
        Assertions.assertEquals("/user/create", requestHandler.splitUrl("GET /user/create?userId=nahowo&password=1234&name=nahyun+park&email=nahowo%40naver.com HTTP/1.1"));
        Assertions.assertEquals("/index.html", requestHandler.splitUrl("GET /index.html HTTP/1.1"));
    }

    @Test
    public void parseQueryStringTest() {
        String body = "userId=nahowo&password=1234&name=nahyun+park&email=nahowo%40naver.com";
        User user = requestHandler.signUp(body);
        Assertions.assertEquals("nahowo", user.getUserId());
        Assertions.assertEquals("1234", user.getPassword());
        Assertions.assertEquals("nahyun+park", user.getName());
        Assertions.assertEquals("nahowo%40naver.com", user.getEmail());
    }

    @Test
    public void readDataTest() {

    }

    @Test
    public void userCreateTest() {

    }
}
