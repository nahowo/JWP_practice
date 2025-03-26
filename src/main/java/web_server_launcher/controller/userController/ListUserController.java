package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JspView;
import web_server_launcher.dao.UserDao;

import java.io.IOException;
import java.sql.SQLException;

public class ListUserController implements Controller {
    public static final Logger log = LoggerFactory.getLogger(ListUserController.class);
    @Override
    public JspView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return new JspView("redirect:/user/loginForm");
        }
        request.setAttribute("loginedUser", UserSessionUtils.getUserFromSession(request.getSession()));
        UserDao userDao = new UserDao();
        request.setAttribute("users", userDao.findAll());
        return new JspView("/user/list.jsp");
    }
}
