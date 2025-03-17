import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web_application_server.webserver.RequestHandler;

import java.net.Socket;

public class SplitUrlTest {
    private RequestHandler requestHandler;
    @BeforeEach
    public void setUp() {
        requestHandler = new RequestHandler(new Socket());
    }
    @Test
    public void splitTest() {
        Assertions.assertEquals("index.html", requestHandler.splitUrl("GET /index.html HTTP/1.1"));
    }
}
