package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.Question;
import web_server_launcher.controller.*;
import web_server_launcher.controller.user.UserSessionUtils;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;

public class UpdateFormQuestionController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(UpdateFormQuestionController.class);
    QuestionDao questionDao = new QuestionDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!UserSessionUtils.isLogined(request.getSession())) {
            throw new IllegalStateException("Can't Change a question without login. ");
        }
        if (!UserSessionUtils.isSameUser(request.getSession(), UserSessionUtils.getUserFromSession(request.getSession()))) {
            throw new IllegalStateException("Can't Change other people's question. ");
        }
        Question question = questionDao.findById(Long.parseLong(request.getParameter("questionId")));

        log.info("question:", question);
        return jspView("/qna/updatedForm.jsp").addObject("question", question);
    }
}
