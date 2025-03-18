import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web_application_server.webserver.RequestHandler;
import web_application_server.model.User;

import java.net.Socket;

public class RequestHandlerTest {
    private RequestHandler requestHandler;
    @BeforeEach
    public void setUp() {
        requestHandler = new RequestHandler(new Socket());
    }
    @Test
    public void getUrlTest() {
        Assertions.assertEquals("/user/create", requestHandler.getUrl("POST /user/create?userId=nahowo&password=1234&name=nahyun+park&email=nahowo%40naver.com HTTP/1.1"));
        Assertions.assertEquals("/index.html", requestHandler.getUrl("GET /index.html HTTP/1.1"));
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
    public void isLoginTest() {
        String line = "Set-Cookie: logined=true";
        Assertions.assertEquals(true, requestHandler.isLogin(line));
    }
}
