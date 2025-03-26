package web_server_launcher.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface View {
    void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
