package web_server_launcher.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JspView implements View {
    private String url;
    public JspView(String url) {
        this.url = url;
    }
    private static final String DEFAULT_REDIRECT_PREFIX = "redirect:";
    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (url.startsWith(DEFAULT_REDIRECT_PREFIX)) {
            response.sendRedirect(url.substring(DEFAULT_REDIRECT_PREFIX.length()));
            return;
        }
        Set<String> keys = model.keySet();
        for (String key : keys) {
            request.setAttribute(key, model.get(key));
        }
        RequestDispatcher rd = request.getRequestDispatcher(url);
        rd.forward(request, response);
    }
}
