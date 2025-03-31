package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Question;
import web_server_launcher.controller.*;
import web_server_launcher.controller.user.UserSessionUtils;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;

public class UpdateQuestionController extends AbstractController {
    QuestionDao questionDao = new QuestionDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            throw new IllegalStateException("Can't Change a question without login. ");
        }
        if (!UserSessionUtils.isSameUser(request.getSession(), UserSessionUtils.getUserFromSession(request.getSession()))) {
            throw new IllegalStateException("Can't Change other people's question. ");
        }
        long questionId = Long.parseLong(request.getParameter("questionId"));
        questionDao.update(request.getParameter("title"), request.getParameter("contents"), questionId);
        Question updatedQuestion = questionDao.findById(questionId);

        return jspView("/qna/show?questionId=" + questionId).addObject("question", updatedQuestion);
    }
}
