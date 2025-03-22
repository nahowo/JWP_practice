package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.User;

import java.io.IOException;

public class ProfileController implements Controller {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return "redirect:/user/login";
        }
        request.setAttribute("user", user);
        return "/user/profile.jsp";
    }
}
