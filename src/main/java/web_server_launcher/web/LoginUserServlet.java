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

@WebServlet("/user/login")
public class LoginUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user == null || !(user.login(request.getParameter("password")))) {
            response.sendRedirect("/user/login_failed.html");
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("/index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/user/login.jsp");
        rd.forward(request, response);
    }
}
