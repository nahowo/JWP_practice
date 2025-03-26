package web_server_launcher.controller.question;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_server_launcher.controller.*;

public class UpdateQuestionController extends AbstractController {
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) {
        return jsonView();
    }
}
