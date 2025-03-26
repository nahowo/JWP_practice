package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.User;
import web_server_launcher.controller.*;
import web_server_launcher.dao.UserDao;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateUserController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(UpdateUserController.class);
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("Can't change other user's information. ");
        }
        User updatedUser = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
        UserDao userDao = new UserDao();
        userDao.update(updatedUser);
        return jspView("redirect:/");
    }
}
