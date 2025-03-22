package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.db.DataBase;

import java.io.IOException;

public class ListUserController implements Controller {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return "redirect:/user/loginForm";
        }
        request.setAttribute("loginedUser", UserSessionUtils.getUserFromSession(request.getSession()));
        request.setAttribute("users", DataBase.findAll());
        return "/user/list.jsp";
    }
}
