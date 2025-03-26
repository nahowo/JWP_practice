package web_server_launcher.controller.question;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JspView;

public class UpdateFormQuestionController implements Controller {
    @Override
    public JspView execute(HttpServletRequest request, HttpServletResponse response) {
        return new JspView("");
    }
}
