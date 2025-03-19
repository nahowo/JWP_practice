package web_application_server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.db.DataBase;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;
import web_application_server.model.User;
import web_application_server.util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

public class ListUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(ListUserController.class);
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (isLogin(request.getHeader("Cookie"))) {
            Collection<User> users = DataBase.findAll();
            StringBuilder sb = userStringBuilder(users);
            response.forwardBody(sb.toString());
        } else {
            response.sendRedirect("/user/login.html");
        }
    }
    private boolean isLogin(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
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
