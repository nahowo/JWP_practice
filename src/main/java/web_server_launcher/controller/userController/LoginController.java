package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.User;
import web_server_launcher.controller.*;
import web_server_launcher.dao.UserDao;

import java.io.IOException;

public class LoginController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao userDao = new UserDao();
        User user = userDao.findByUserId(request.getParameter("userId"));
        if (user == null || !(user.login(request.getParameter("password")))) {
            return jspView("/user/login.jsp").addObject("loginFailed", true);
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        return jspView("redirect:/");
    }
}
