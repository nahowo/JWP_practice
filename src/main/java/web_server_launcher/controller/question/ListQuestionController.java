package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.Question;
import web_server_launcher.controller.AbstractController;
import web_server_launcher.controller.ModelAndView;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;
import java.util.List;

public class ListQuestionController extends AbstractController {
    public static final Logger log = LoggerFactory.getLogger(ListQuestionController.class);
    private QuestionDao questionDao = new QuestionDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Question> questions = questionDao.findAll();

        return jsonView().addObject("questions", questions);
    }
}
