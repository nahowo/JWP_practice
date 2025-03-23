package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_server_launcher.dao.UserDao;

import java.io.IOException;
import java.sql.SQLException;

public class ListUserController implements Controller {
    public static final Logger log = LoggerFactory.getLogger(ListUserController.class);
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return "redirect:/user/loginForm";
        }
        request.setAttribute("loginedUser", UserSessionUtils.getUserFromSession(request.getSession()));
        UserDao userDao = new UserDao();
        try {
            request.setAttribute("users", userDao.findAll());
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return "/user/list.jsp";
    }
}
