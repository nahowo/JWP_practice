package web_application_server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;
import web_application_server.model.User;
import web_application_server.session.HttpSession;
import web_application_server.util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ListUserController.class);
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (isLogin(request.getSession())) {
            Collection<User> users = DataBase.findAll();
            StringBuilder sb = userStringBuilder(users);
            response.forwardBody(sb.toString());
        } else {
            response.sendRedirect("/user/login.html");
        }
    }
    private boolean isLogin(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return true;
    }
    private StringBuilder userStringBuilder(Collection<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table borders='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb;
    }
}
