package web_server_launcher.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_server_launcher.controller.*;
import web_server_launcher.controller.AnswerController;
import web_server_launcher.controller.QuestionController;
import web_server_launcher.controller.UserController;
import web_server_launcher.controller.view.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMapping {
    private static final Logger log = LoggerFactory.getLogger(RequestMapping.class);
    private Map<String, ControllerMethod> mappings = new HashMap<>();

    void initMapping() {
        List<Controller> controllers = List.of(new UserController(), new QuestionController(), new AnswerController(), new HomeController());
        for (Controller controller : controllers) {
            Method[] methods = controller.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Mapping.class)) {
                    Mapping annotation = method.getAnnotation(Mapping.class);
                    String url = annotation.value();
                    if (mappings.containsKey(url)) {
                        throw new IllegalArgumentException("경로 중복: " + url + " -> " + method.getName() + " & " + mappings.get(url).getClass().getSimpleName());
                    }
                    mappings.put(url, new ControllerMethod(controller, method));
                }
            }
        }
        log.info("Initialized Request Mapping!");
    }

    public ControllerMethod findController(String url) {
        return mappings.get(url);
    }

    public static class ControllerMethod {
        private final Controller controller;
        private final Method method;

        public ControllerMethod(Controller controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == HttpServletRequest.class) {
                    args[i] = request;
                } else if (parameterTypes[i] == HttpServletResponse.class) {
                    args[i] = response;
                }
            }

            ModelAndView modelAndView = (ModelAndView) method.invoke(controller, args);
            return modelAndView;
        }
    }
}
