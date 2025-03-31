package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Question;
import web_application_server.model.Result;
import web_server_launcher.controller.AbstractController;
import web_server_launcher.controller.ModelAndView;
import web_server_launcher.controller.user.UserSessionUtils;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.dao.QuestionDao;
import web_server_launcher.dao.UserDao;
import web_server_launcher.service.QnaService;

import java.io.IOException;

public class DeleteQuestionApiController extends AbstractController {
    private QuestionDao questionDao = new QuestionDao();
    private AnswerDao answerDao = new AnswerDao();
    private QnaService qnaService = new QnaService();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            throw new IllegalStateException("Can't delete a question without login. ");
        }
        if (!UserSessionUtils.isSameUser(request.getSession(), UserSessionUtils.getUserFromSession(request.getSession()))) {
            throw new IllegalStateException("Can't delete other people's question. ");
        }
        Question question = questionDao.findById(Long.parseLong(request.getParameter("questionId")));
        String name = UserSessionUtils.getUserFromSession(request.getSession()).getName();
        if (!qnaService.hasZeroCountOfAnswers(question) || !qnaService.isAllWrittenByLoginedUser(name, answerDao.findAllById(question.getQuestionId()))) {
            throw new IllegalStateException("Cat't delete a Question with answers");
        }
        questionDao.delete(question.getQuestionId());
        return jsonView().addObject("result", Result.ok());
    }
}
