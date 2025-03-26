package web_server_launcher.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ForwardController extends AbstractController {
    private String forwardUrl;

    public ForwardController(String forwardUrl) {
        this.forwardUrl = forwardUrl;
        if (forwardUrl == null) {
            throw new NullPointerException("forwardUrl is null");
        }
    }

    @Override
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return jspView(forwardUrl);
    }
}
