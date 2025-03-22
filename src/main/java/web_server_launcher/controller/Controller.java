package web_server_launcher.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Controller {
    String execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
