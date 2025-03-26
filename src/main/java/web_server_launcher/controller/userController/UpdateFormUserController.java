package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.User;
import web_server_launcher.controller.*;

import java.io.IOException;

public class UpdateFormUserController extends AbstractController {
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("Can't change other user's information. ");
        }
        return jspView("/user/updatedForm.jsp").addObject("user", user);
    }
}
