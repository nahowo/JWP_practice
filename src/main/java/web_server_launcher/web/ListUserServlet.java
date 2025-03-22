package web_server_launcher.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web_application_server.db.DataBase;
import web_application_server.model.User;

import java.io.IOException;

//@WebServlet("/user/list")
public class ListUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object value = session.getAttribute("user");
        if (value == null) {
            response.sendRedirect("/user/login.jsp");
            return;
        }
        User user = (User) value;
        request.setAttribute("loginedUser", user);
        request.setAttribute("users", DataBase.findAll());
        RequestDispatcher rd = request.getRequestDispatcher("/user/list.jsp");
        rd.forward(request, response);
    }
}
