import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;

import java.io.*;

public class HttpResponseTest {
    private String resourceDirectory = "./src/test/resources/";
    private HttpResponse httpResponse;
    @Test
    public void responseForward() throws Exception {
        httpResponse = new HttpResponse(createOutputStream("Http_forward.txt"));
        httpResponse.forward("/index.html");
    }

    @Test
    public void responseRedirect() throws Exception {
        httpResponse = new HttpResponse(createOutputStream("Http_redirect.txt"));
        httpResponse.sendRedirect("/index.html");
    }

    @Test
    public void responseCookies() throws Exception {
        httpResponse = new HttpResponse(createOutputStream("Http_Cookies.txt"));
        httpResponse.addHeader("Set-Cookie", "logined=true");
        httpResponse.sendRedirect("/index.html");
    }

    private OutputStream createOutputStream(String fileName) throws FileNotFoundException {
        return new FileOutputStream(new File(resourceDirectory + fileName));
    }
}
