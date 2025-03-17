import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web_application_server.webserver.RequestHandler;
import web_application_server.model.User;

import java.net.Socket;

public class RequestHandlerTest {
    private RequestHandler requestHandler;
    private User user;
    @BeforeEach
    public void setUp() {
        requestHandler = new RequestHandler(new Socket());
    }
    @Test
    public void getUrlTest() {
        Assertions.assertEquals("index.html", requestHandler.splitUrl("GET /index.html HTTP/1.1"));
    }

    @Test
    public void userCreateTest() {

    }
}
