package web_application_server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;
import web_application_server.model.User;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user == null || !(user.login(request.getParameter("password")))) {
            response.sendRedirect("/user/login_failed.html");
        } else {
            if (!(request.getHeader("Cookie").contains("logined=true"))) {
                response.addHeader("Set-Cookie", "logined=true; Path=/");
            }
            response.sendRedirect("/index.html");
        }
    }
}
