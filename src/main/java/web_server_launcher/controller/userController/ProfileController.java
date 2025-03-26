package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.User;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JspView;

import java.io.IOException;

public class ProfileController implements Controller {
    @Override
    public JspView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return new JspView("redirect:/user/login");
        }
        request.setAttribute("user", user);
        return new JspView("/user/profile.jsp");
    }
}
