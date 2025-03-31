package web_server_launcher.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.User;
import web_server_launcher.controller.*;

import java.io.IOException;

public class ProfileController extends AbstractController {
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return jspView("redirect:/user/login");
        }
        return jspView("/user/profile.jsp").addObject("user", user);
    }
}
