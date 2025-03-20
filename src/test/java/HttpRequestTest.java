import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web_application_server.http.HttpRequest;
import web_application_server.webserver.RequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

public class HttpRequestTest {
    private String resourceDirectory = "./src/test/resources";
//    @Test
//    public void requestPostTest() throws Exception{
//        InputStream in = new FileInputStream(new File(resourceDirectory + "/Http_POST.txt"));
//        HttpRequest httpRequest = new HttpRequest(in);
//        Assertions.assertEquals("POST", httpRequest.getMethod());
//        Assertions.assertEquals("/user/create", httpRequest.getPath());
//        Assertions.assertEquals("keep-alive", httpRequest.getHeader("Connection"));
//        Assertions.assertEquals("nahowo", httpRequest.getParameter("userId"));
//    }
}
