package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import web_application_server.model.Answer;
import web_application_server.model.Question;
import web_application_server.model.User;
import web_server_launcher.controller.*;
import web_server_launcher.controller.userController.UserSessionUtils;

import java.io.IOException;
import java.util.Date;

public class CreateQuestionController extends AbstractController {
    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return jspView("");
    }
}
