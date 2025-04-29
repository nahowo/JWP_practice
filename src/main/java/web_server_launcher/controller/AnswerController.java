package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Answer;
import web_application_server.model.Result;
import web_server_launcher.controller.view.ModelAndView;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.mvc.Mapping;

import java.io.IOException;
import java.util.List;

public class AnswerController extends AbstractController {
    private AnswerDao answerDao = new AnswerDao();

    @Mapping("/api/qna/addAnswer")
    public ModelAndView addAnswer(HttpServletRequest request) throws ServletException, IOException {
        Answer answer = new Answer(request.getParameter("writer"), request.getParameter("contents"), Long.parseLong(request.getParameter("questionId")));
        Answer savedAnswer = answerDao.insert(answer);

        return jsonView().addObject("answer", List.of(savedAnswer));
    }

    @Mapping("/api/qna/deleteAnswer")
    public ModelAndView deleteAnswer(HttpServletRequest request) throws ServletException, IOException {
        long answerId = Long.parseLong(request.getParameter("answerId"));
        AnswerDao answerDao = new AnswerDao();
        answerDao.delete(answerId);

        return jsonView().addObject("result", Result.ok());
    }
}
