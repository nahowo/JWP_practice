package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web_application_server.db.DataBase;
import web_application_server.model.User;

import java.io.IOException;

public class LoginController implements Controller {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user == null || !(user.login(request.getParameter("password")))) {
            return "/user/login_failed.html";
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        return "redirect:/";
    }
}
