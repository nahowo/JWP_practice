package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web_application_server.model.User;
import web_server_launcher.controller.util.UserSessionUtils;
import web_server_launcher.controller.view.ModelAndView;
import web_server_launcher.dao.UserDao;
import web_server_launcher.mvc.Mapping;

import java.io.IOException;

public class UserController extends AbstractController {
    @Mapping("/user/list")
    public ModelAndView listUser(HttpServletRequest request) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            return jspView("redirect:/user/loginForm");
        }
        UserDao userDao = new UserDao();
        return jspView("/user/list.jsp").addObject("users", userDao.findAll()).addObject("loginedUser", UserSessionUtils.getUserFromSession(request.getSession()));
    }

    @Mapping("/user/create")
    public ModelAndView createUser(HttpServletRequest request) throws ServletException, IOException {
        User user = new User(request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        UserDao userDao = new UserDao();
        userDao.insert(user);
        return jspView("redirect:/");
    }

    @Mapping("/user/form")
    public ModelAndView userForm() throws Exception {
        return jspView("/user/form.jsp");
    }

    @Mapping("/user/update")
    public ModelAndView updateUser(HttpServletRequest request) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("Can't change other user's information. ");
        }
        User updatedUser = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
        UserDao userDao = new UserDao();
        userDao.update(updatedUser);
        return jspView("redirect:/");
    }

    @Mapping("/user/updateForm")
    public ModelAndView updateFormUser(HttpServletRequest request) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("Can't change other user's information. ");
        }
        return jspView("/user/updatedForm.jsp").addObject("user", user);
    }

    @Mapping("/user/login")
    public ModelAndView login(HttpServletRequest request) throws ServletException, IOException {
        UserDao userDao = new UserDao();
        User user = userDao.findByUserId(request.getParameter("userId"));
        if (user == null || !(user.login(request.getParameter("password")))) {
            return jspView("/user/login.jsp").addObject("loginFailed", true);
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        return jspView("redirect:/");
    }

    @Mapping("/user/loginForm")
    public ModelAndView loginForm() throws Exception {
        return jspView("/user/login.jsp");
    }

    @Mapping("/user/profile")
    public ModelAndView profile(HttpServletRequest request) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return jspView("redirect:/user/login");
        }
        return jspView("/user/profile.jsp").addObject("user", user);
    }

    @Mapping("/user/logout")
    public ModelAndView logout(HttpServletRequest request) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        return jspView("redirect:/");
    }
}
