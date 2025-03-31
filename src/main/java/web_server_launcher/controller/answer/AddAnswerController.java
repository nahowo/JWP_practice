package web_server_launcher.controller.answer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.Answer;
import web_server_launcher.controller.*;
import web_server_launcher.dao.AnswerDao;

import java.io.IOException;
import java.util.List;

public class AddAnswerController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(AddAnswerController.class);
    private AnswerDao answerDao = new AnswerDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Answer answer = new Answer(request.getParameter("writer"), request.getParameter("contents"), Long.parseLong(request.getParameter("questionId")));
        log.info("data: ", answer.getWriter(), answer.getContents(), answer.getQuestionId());
        Answer savedAnswer = answerDao.insert(answer);

        return jsonView().addObject("answer", List.of(savedAnswer));
    }
}
