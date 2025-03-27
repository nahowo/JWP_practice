package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Question;
import web_server_launcher.controller.*;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;

public class CreateQuestionController extends AbstractController {
    private QuestionDao questionDao = new QuestionDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Question question = new Question(request.getParameter("writer"), request.getParameter("title"), request.getParameter("contents"));
        questionDao.insert(question);

        return jspView("redirect:/").addObject("questions", questionDao.findAll());
    }
}
