package web_server_launcher.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_server_launcher.controller.*;
import web_server_launcher.controller.answer.AddAnswerController;
import web_server_launcher.controller.answer.DeleteAnswerController;
import web_server_launcher.controller.question.CreateQuestionController;
import web_server_launcher.controller.question.ListQuestionController;
import web_server_launcher.controller.question.QuestionFormController;
import web_server_launcher.controller.question.ShowController;
import web_server_launcher.controller.userController.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Logger log = LoggerFactory.getLogger(RequestMapping.class);
    private Map<String, Controller> mappings = new HashMap<>();

    void initMapping() {
        mappings.put("/", new HomeController());
        mappings.put("/user/form", new ForwardController("/user/form.jsp"));
        mappings.put("/user/loginForm", new ForwardController("/user/login.jsp"));
        mappings.put("/user/list", new ListUserController());
        mappings.put("/user/login", new LoginController());
        mappings.put("/user/create", new CreateUserController());
        mappings.put("/user/logout", new LogoutController());
        mappings.put("/user/profile", new ProfileController());
        mappings.put("/user/update", new UpdateUserController());
        mappings.put("/user/updateForm", new UpdateFormUserController());
        mappings.put("/qna/show", new ShowController());
        mappings.put("/api/qna/addAnswer", new AddAnswerController());
        mappings.put("/api/qna/deleteAnswer", new DeleteAnswerController());
        mappings.put("/qna/form", new QuestionFormController());
        mappings.put("/qna/create", new CreateQuestionController());
        mappings.put("/api/qna/list", new ListQuestionController());

        log.info("Initialized Request Mapping!");
    }

    public Controller findController(String url) {
        return mappings.get(url);
    }

    void put(String url, Controller controller) {
        mappings.put(url, controller);
    }
}
