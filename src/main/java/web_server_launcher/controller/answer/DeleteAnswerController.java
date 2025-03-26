package web_server_launcher.controller.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Result;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JsonView;
import web_server_launcher.dao.AnswerDao;

import java.io.IOException;
import java.io.PrintWriter;

public class DeleteAnswerController implements Controller {
    @Override
    public JsonView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long answerId = Long.parseLong(request.getParameter("answerId"));
        AnswerDao answerDao = new AnswerDao();
        answerDao.delete(answerId);

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(mapper.writeValueAsString(Result.ok()));
        return null;
    }
}
