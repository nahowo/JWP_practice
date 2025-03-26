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

public class HomeController extends AbstractController {
    private QuestionDao questionDao = new QuestionDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return jspView("home.jsp").addObject("questions", questionDao.findAll());
    }
}
