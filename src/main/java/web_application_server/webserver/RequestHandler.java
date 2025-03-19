package web_application_server.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_application_server.controller.Controller;
import web_application_server.controller.RequestMapping;
import web_application_server.db.DataBase;
import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;
import web_application_server.model.User;
import web_application_server.util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

public class RequestHandler extends Thread {
    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        log.debug("New Client Connected. Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            Controller controller = RequestMapping.getController(request.getPath());
            if (controller == null) {
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            } else {
                controller.service(request, response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
