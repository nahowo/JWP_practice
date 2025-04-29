package web_server_launcher.controller;

import web_server_launcher.controller.view.JsonView;
import web_server_launcher.controller.view.JspView;
import web_server_launcher.controller.view.ModelAndView;

public abstract class AbstractController implements Controller {
    protected ModelAndView jspView(String forwardUrl) {
        return new ModelAndView(new JspView(forwardUrl));
    }

    protected ModelAndView jsonView() {
        return new ModelAndView(new JsonView());
    }
}
