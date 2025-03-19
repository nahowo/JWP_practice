package web_application_server.controller;

import web_application_server.http.HttpRequest;
import web_application_server.http.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
