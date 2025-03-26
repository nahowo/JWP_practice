package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_server_launcher.controller.*;
import web_server_launcher.dao.UserDao;

import java.io.IOException;
import java.sql.SQLException;

public class ListUserController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(ListUserController.class);
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return jspView("redirect:/user/loginForm");
        }
        request.setAttribute("loginedUser", UserSessionUtils.getUserFromSession(request.getSession()));
        UserDao userDao = new UserDao();
        return jspView("/user/list.jsp").addObject("users", userDao.findAll());
    }
}
