package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_server_launcher.controller.view.ModelAndView;
import web_server_launcher.dao.QuestionDao;
import web_server_launcher.mvc.Mapping;

import java.io.IOException;

public class HomeController extends AbstractController {
    private QuestionDao questionDao = new QuestionDao();
    @Mapping("/")
    public ModelAndView home() throws ServletException, IOException {
        return jspView("home.jsp").addObject("questions", questionDao.findAll());
    }
}
