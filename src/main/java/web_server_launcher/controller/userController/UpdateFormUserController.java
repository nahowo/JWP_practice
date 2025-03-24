package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.User;
import web_server_launcher.controller.Controller;

import java.io.IOException;

public class UpdateFormUserController implements Controller {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("Can't change other user's information. ");
        }
        request.setAttribute("user", user);
        return "/user/updatedForm.jsp";
    }
}
