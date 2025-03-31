package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.User;
import web_server_launcher.controller.*;
import web_server_launcher.controller.user.UserSessionUtils;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;

public class ShowController extends AbstractController {
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long questionId = Long.parseLong(request.getParameter("questionId"));
        QuestionDao questionDao = new QuestionDao();
        AnswerDao answerDao = new AnswerDao();
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        return jspView("/qna/show.jsp").addObject("question", questionDao.findById(questionId)).addObject("answers", answerDao.findAllById(questionId)).addObject("loginedUser", user);
    }
}
