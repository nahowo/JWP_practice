package web_server_launcher.mvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_server_launcher.controller.Controller;
import web_server_launcher.controller.View;

import java.io.IOException;

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

        Controller controller = rm.findController(requestUrl);
        try {
            View view = controller.execute(request, response);
            view.render(request, response);
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
//        try {
//            String viewName = controller.execute(request, response);
//            if (viewName != null) {
//                move(viewName, request, response);
//            }
//        } catch (Throwable e) {
//            log.error(e.getMessage());
//        }
    }

    private void move(String viewName, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        if (viewName.startsWith(DEFAULT_REDIRECT_PREFIX)) {
            response.sendRedirect(viewName.substring(DEFAULT_REDIRECT_PREFIX.length()));
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher(viewName);
        rd.forward(request, response);
    }
}
