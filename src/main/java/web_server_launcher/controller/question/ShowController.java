package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Question;
import web_server_launcher.controller.Controller;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;

public class ShowController implements Controller {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long questionId = Long.parseLong(request.getParameter("questionId"));
        QuestionDao questionDao = new QuestionDao();
        AnswerDao answerDao = new AnswerDao();
        request.setAttribute("question", questionDao.findById(questionId));
        request.setAttribute("answers", answerDao.findAllById(questionId));
        return "/qna/show.jsp";
    }
}
