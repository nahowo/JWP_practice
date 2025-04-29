package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Question;
import web_application_server.model.Result;
import web_application_server.model.User;
import web_server_launcher.controller.util.UserSessionUtils;
import web_server_launcher.controller.view.ModelAndView;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.dao.QuestionDao;
import web_server_launcher.mvc.Mapping;
import web_server_launcher.service.QnaService;

import java.io.IOException;
import java.util.List;

public class QuestionController extends AbstractController {
    private final QuestionDao questionDao = new QuestionDao();
    private final AnswerDao answerDao = new AnswerDao();
    private final QnaService qnaService = new QnaService();

    @Mapping("/qna/show")
    public ModelAndView showQuestion(HttpServletRequest request) throws ServletException, IOException {
        Long questionId = Long.parseLong(request.getParameter("questionId"));
        QuestionDao questionDao = new QuestionDao();
        AnswerDao answerDao = new AnswerDao();
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        return jspView("/qna/show.jsp").addObject("question", questionDao.findById(questionId)).addObject("answers", answerDao.findAllById(questionId)).addObject("loginedUser", user);
    }
    @Mapping("/api/qna/list")
    public ModelAndView listQuestion() throws ServletException, IOException {
        List<Question> questions = questionDao.findAll();

        return jsonView().addObject("questions", questions);
    }

    @Mapping("/qna/create")
    public ModelAndView createQuestion(HttpServletRequest request) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return jspView("redirect:/user/loginForm");
        }
        Question question = new Question(user.getUserId(), request.getParameter("title"), request.getParameter("contents"));
        questionDao.insert(question);

        return jspView("redirect:/").addObject("questions", questionDao.findAll());
    }

    @Mapping("/qna/form")
    public ModelAndView questionForm(HttpServletRequest request) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return jspView("redirect:/user/loginForm");
        }
        return jspView("/qna/form.jsp");
    }

    @Mapping("/qna/update")
    public ModelAndView updateQuestion(HttpServletRequest request) throws ServletException, IOException {
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

    @Mapping("/qna/updateForm")
    public ModelAndView updateFormQuestion(HttpServletRequest request) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            throw new IllegalStateException("Can't Change a question without login. ");
        }
        if (!UserSessionUtils.isSameUser(request.getSession(), UserSessionUtils.getUserFromSession(request.getSession()))) {
            throw new IllegalStateException("Can't Change other people's question. ");
        }
        Question question = questionDao.findById(Long.parseLong(request.getParameter("questionId")));

        return jspView("/qna/updatedForm.jsp").addObject("question", question);
    }

    @Mapping("/api/qna/deleteQuestion")
    public ModelAndView deleteQuestionApi(HttpServletRequest request) throws ServletException, IOException {
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

    @Mapping("/qna/deleteQuestion")
    public ModelAndView deleteQuestion(HttpServletRequest request) throws ServletException, IOException {
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
        return jspView("/").addObject("questions", questionDao.findAll());
    }
}
