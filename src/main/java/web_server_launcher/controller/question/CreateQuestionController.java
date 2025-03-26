package web_server_launcher.controller.question;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import web_application_server.model.Answer;
import web_application_server.model.Question;
import web_application_server.model.User;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JspView;
import web_server_launcher.controller.userController.UserSessionUtils;

import java.io.IOException;
import java.util.Date;

public class CreateQuestionController implements Controller {
    @Override
    public JspView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return new JspView("");
    }
}
