package web_server_launcher.controller.userController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.JspView;

import java.io.IOException;

public class LogoutController implements Controller {
    @Override
    public JspView execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        return new JspView("redirect:/");
    }
}
