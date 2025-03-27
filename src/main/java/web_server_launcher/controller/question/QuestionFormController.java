package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_application_server.model.Question;
import web_application_server.model.User;
import web_server_launcher.controller.AbstractController;
import web_server_launcher.controller.ModelAndView;
import web_server_launcher.controller.userController.UserSessionUtils;
import web_server_launcher.dao.QuestionDao;

import java.io.IOException;

public class QuestionFormController extends AbstractController {
    private QuestionDao questionDao = new QuestionDao();
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = UserSessionUtils.getUserFromSession(request.getSession());
        if (user == null) {
            return jspView("redirect:/user/loginForm");
        }
        return jspView("/qna/form.jsp");
    }
}
