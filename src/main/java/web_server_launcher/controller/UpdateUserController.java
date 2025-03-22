package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.db.DataBase;
import web_application_server.model.User;

import java.io.IOException;

public class UpdateUserController implements Controller{
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("Can't change other user's information. ");
        }
        User updatedUser = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
        user.update(updatedUser);
        return "redirect:/";
    }
}
