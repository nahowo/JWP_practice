package web_server_launcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JsonView implements View {
    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        for (String key : model.keySet()) {
            out.print(mapper.writeValueAsString(model.get(key)));
        }
    }
}
