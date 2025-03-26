package web_server_launcher.controller.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Result;
import web_server_launcher.controller.*;
import web_server_launcher.dao.AnswerDao;

import java.io.IOException;
import java.io.PrintWriter;

public class DeleteAnswerController extends AbstractController {
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long answerId = Long.parseLong(request.getParameter("answerId"));
        AnswerDao answerDao = new AnswerDao();
        answerDao.delete(answerId);

        return jsonView().addObject("", Result.ok());
    }
}
