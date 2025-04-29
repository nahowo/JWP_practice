package web_server_launcher.mvc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_server_launcher.controller.view.ModelAndView;
import web_server_launcher.controller.view.View;

import java.io.IOException;

import static web_server_launcher.mvc.RequestMapping.*;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private static final String DEFAULT_REDIRECT_PREFIX = "redirect:";
    private RequestMapping rm;

    @Override
    public void init() throws ServletException {
        rm = new RequestMapping();
        rm.initMapping();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();
        log.debug("Method: {}, RequestURI: {}", request.getMethod(), requestUrl);

        try {
            ControllerMethod controller = rm.findController(requestUrl);
            ModelAndView mv = controller.invoke(request, response);
            View view = mv.getView();
            view.render(mv.getModel(), request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
