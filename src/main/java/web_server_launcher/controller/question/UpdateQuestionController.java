package web_server_launcher.controller.question;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JsonView;

public class UpdateQuestionController implements Controller {
    @Override
    public JsonView execute(HttpServletRequest request, HttpServletResponse response) {
        return new JsonView();
    }
}
