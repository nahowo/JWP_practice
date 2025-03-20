import org.junit.jupiter.api.Test;
import web_application_server.controller.CreateUserController;
import web_application_server.controller.ListUserController;
import web_application_server.controller.LoginController;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;

import java.io.*;

public class ControllerTest {
    private String resourceDirectory = "./src/test/resources";
    @Test
    public void createUserControllerTest() throws Exception{
        CreateUserController controller = new CreateUserController();
        InputStream in = new FileInputStream(new File(resourceDirectory + "/Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);
        HttpResponse response = new HttpResponse(createtOutputStream("/Controller_create.txt"));
        controller.service(request, response);
    }
// deprecated
//    @Test
//    public void loginUserControllerTest() throws Exception {
//        LoginController controller = new LoginController();
//        InputStream in = new FileInputStream(new File(resourceDirectory + "/Http_login.txt"));
//        HttpRequest request = new HttpRequest(in);
//        HttpResponse response = new HttpResponse(createtOutputStream("/Controller_login.txt"));
//        controller.service(request, response);
//    }
//
//    @Test
//    public void listUserControllerTest() throws Exception {
//        ListUserController controller = new ListUserController();
//        InputStream in = new FileInputStream(new File(resourceDirectory + "/Http_user_list.txt"));
//        HttpRequest request = new HttpRequest(in);
//        HttpResponse response = new HttpResponse(createtOutputStream("/Controller_listUser.txt"));
//        controller.service(request, response);
//    }

    private OutputStream createtOutputStream(String fileName) throws FileNotFoundException {
        return new FileOutputStream(new File(resourceDirectory + fileName));
    }
}
