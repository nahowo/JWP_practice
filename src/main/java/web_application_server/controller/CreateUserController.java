package web_application_server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;
import web_application_server.model.User;

public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        log.debug("user: " + user);
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }
}
