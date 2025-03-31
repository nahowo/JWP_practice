package web_server_launcher.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.User;
import web_server_launcher.controller.*;
import web_server_launcher.dao.UserDao;

import java.io.IOException;

public class CreateUserController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User(request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        UserDao userDao = new UserDao();
        userDao.insert(user);
        return jspView("redirect:/");
    }
}
