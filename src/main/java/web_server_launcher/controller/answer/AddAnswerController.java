package web_server_launcher.controller.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.model.Answer;
import web_server_launcher.controller.Controller;
import web_server_launcher.dao.AnswerDao;

import java.io.IOException;
import java.io.PrintWriter;

public class AddAnswerController implements Controller {
    public static final Logger log = LoggerFactory.getLogger(AddAnswerController.class);
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Answer answer = new Answer(request.getParameter("writer"), request.getParameter("contents"), Long.parseLong(request.getParameter("questionId")));
        AnswerDao answerDao = new AnswerDao();
        Answer savedAnswer = answerDao.insert(answer);
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(mapper.writeValueAsString(savedAnswer));
        log.info("data: " + savedAnswer.getWriter() + ", " + savedAnswer.getContents());
        return null;
    }
}
