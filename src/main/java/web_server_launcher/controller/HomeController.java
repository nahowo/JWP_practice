package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.db.DataBase;
import web_application_server.model.Question;
import web_application_server.model.User;
import web_server_launcher.dao.QuestionDao;
import web_server_launcher.dao.UserDao;

import java.io.IOException;
import java.util.List;

public class HomeController implements Controller {
    @Override
    public JspView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao userDao = new UserDao();
        QuestionDao questionDao = new QuestionDao();
        request.setAttribute("users", userDao.findAll());
        request.setAttribute("questions", questionDao.findAll());
        return new JspView("home.jsp");
    }
}
