# 서블릿/JSP를 사용한 리팩토링 2

# Tomcat Embedded 사용하기

- [링크](https://tomcat.apache.org/download-11.cgi)에서 톰캣 11을 다운받을 수 있다.
- jar 파일을 직접 프로젝트 디렉토리에 다운받으므로 Embedded 버전, tar.gz를 받았다.
- 다운받은 파일의 압축을 풀고 프로젝트에 lib 디렉토리를 생성해 붙여넣으면 된다.
- gradle 빌드를 위해 build.gradle에 아래 코드를 추가하고 다시 빌드를 수행했다.

    ```yaml
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:11.0.5'
    implementation 'org.apache.tomcat.embed:tomcat-embed-jasper:11.0.5'
    implementation 'jakarta.servlet:jakarta.servlet-api:6.1.0'
    implementation 'jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.2'
    implementation 'org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.0'
    ```

    - javax가 아니라 jakarta에서 가져와야 한다.
    - glassfish.web은 jstl 표준의 구현체이다.

### WebServerLauncher

- 5장까지의 실습에서 작성했던 RequestHandler역할을 수행할 WebServerLauncher를 작성한다.

```java
public static void main(String[] args) throws Exception{
        String webappDirLocation = "webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.addWebapp("/",
                new File(webappDirLocation).getAbsolutePath());
        log.info("configuring app with baseDir: " + new File("./" + webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getConnector();
        tomcat.getServer().await();
    }
```

### setPort(int port)

> 기본 커넥터에 대한 포트를 설정한다. **기본 커넥터는 getConnector()가 호출되어야만 생성된다.**
>
- 기본 커넥터를 설정하지 않으면 서버가 실행되어도 브라우저에서 접근이 불가능하다.

### addWebApp(String contextPath, URL source)

> 특정 WAR 파일을 호스트의 appBase에 복사하고 해당 addWebAppBase(String, String)을 호출한다.
>
- “/” 디렉토리(루트 디렉토리)에 복사한 WAR 파일을 생성한다. 생성된 디렉토리에는 jsp 등 동적으로 생성된 파일들이 존재한다. 서버를 종료해도 사라지지 않으며, 원본 파일(스크립트)이 수정되는 경우에만 업데이트된다.

### addWebApp(String contextPath,  String docBase)

> 호스트의 appBase에 웹 애플리케이션을 추가한다.
>

### start()

> 서버를 시작한다.
>

### getConnector()

> 내장 tocat이 사용하는 기본 HTTP 커넥터를 가져온다. 정의된 커넥터가 없다면 현재 tomcat 인스턴스의 포트와 주소를 이용해 기본 커넥터를 생성한다. 기본 커넥터 생성은 setConnector()를 통해 할 수 있다.
>

### getServer()

> server 객체를 가져온다. 리스너 등의 커스터마이징을 할 수 있다.
>

### Server.await()

> shutdown 커맨드가 발생할 때까지 대기하다가 발생 시 리턴한다.
>

## RequestHandler의 run()과 Tomcat 비교

- 분석에 도움이 되는 intelliJ 단축키
    - 구현체 코드로 이동하기: Cmd + Option + B
    - 부모 클래스로 이동하기: Cmd + B

### WebServer

```java
public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            log.info("Web Application Server started at {} port. ", port);
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                RequestHandler requestHandler = new RequestHandler(connection);
                requestHandler.start();
            }
        }
    }
}
```

### RequestHandle.run()

```java
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
```

- WebServer에서 while문을 돌리고 요청이 발생하면 RequestHandler 객체를 생성하고 run()을 수행한다.
- RequestHandler 인스턴스에서 request, response 객체를 생성한다.
- 요청에 따라 controller 객체를 생성하고 service() 메서드를 수행한다.

### Tomcat WebServerLauncher

```java
public class WebServerLauncher {
    private static final Logger log = LoggerFactory.getLogger(WebServerLauncher.class);
    public static void main(String[] args) throws Exception{
        String webappDirLocation = "webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.addWebapp("",
                new File(webappDirLocation).getAbsolutePath());
        log.info("configuring app with baseDir: " + new File("./" + webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getConnector();
        tomcat.getServer().await();
    }
}
```

- start()로 Tomcat이 시작되고, Connector객체를 통해 포트에서의 클라이언트 요청을 대기하게 된다.
- 클라이언트에서 HTTP 요청이 발생하면 Tomcat이 요청을 수신하고 Connector가 처리를 시작한다.
- Connector가 받은 요청을 HTTP 형식으로 파싱한 뒤 Engine(Catalina.Engine)에 전달한다.
- 요청이 다시 Host로 전달(localhost 등)된다.
- 요청이 다시 Context로 전달된다. Context는 addWebapp()에서 설정했던 웹 애플리케이션의 루트 디렉토리로 설정되고, 해당 디렉토리 안에서 요청을 찾는다([참고](https://github.com/nahowo/JWP_practice/blob/main/docs/chapter6.md#Tomcat-%EA%B2%BD%EB%A1%9C-%EB%AC%B8%EC%A0%9C)). 
- 요청이 들어오면 디렉토리 안의 클래스 중 @WebServlet으로 정의된 url과 매칭시켜 해당 클래스의 doPost/doGet을 실행한다.

## 트러블슈팅

### Tomcat 연결 문제 해결하기

https://jiggyjiggy.tistory.com/74

- Tomcat 11을 사용해 실습을 수행했더니 서버가 실행은 되지만 브라우저에서 접근이 안 되는 문제가 발생했다.

    ```java
    String webappDirLocation = "webapp/";
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);
    
            tomcat.addWebapp("/",
                    new File(webappDirLocation).getAbsolutePath());
            log.info("configuring app with baseDir: " + new File("./" + webappDirLocation).getAbsolutePath());
    
            tomcat.start();
            tomcat.getConnector();
            tomcat.getServer().await();
    ```

    - 위의 tomcat.getConnector()를 추가해야 한다.
    - Tomcat 8에서는 `start()` 메서드에서 `getConnector()` 메서드를 실행하지만, Tomcat 11에서는 `getConnector()` 메서드 실행 부분이 사라졌다.

+의존성 버전을 잘 확인하자([링크](https://tomcat.apache.org/whichversion.html)).

### Tomcat 경로 문제

- Tomcat은 자바 클래스를 인식하는 디폴트 디렉토리가 있는데, webapps/WEB-INF/classes 디렉토리에 컴파일된 .class 파일만 인식할 수 있다.
- 빌드 설정이 gradle로 되어 있다면 intelliJ는 컴파일한 클래스 파일을 루트 디렉토리 > build > classes 파일에 자동으로 컴파일한다. IntelliJ IDEA > settings > Buile, Execution, Deployment > Build Tools > Gradle에서 Build and run using: 을 gradle 대신 IntelliJ IDEA로 변경해주면 된다.
- 빌드 설정을 변경 후 컴파일할 클래스 경로를 지정해야 한다. File > Project Structure > Project Settings > Project에서 Compiler output을 지정한다. 이때 지정된 디렉토리는 tomcat.addWebapp()에서 설정했던 디렉토리 하위에 위치해야 한다.
    - 즉 WebServerLauncher에서 `tomcat.addWebapp(”/”, “webapp/”);`이라는 코드를 작성했다면 Compiler output은 `{프로젝트명}/wabapp/~` 이어야 한다.

---
# HttpSession 실습

## 요구사항

### String getId()

> 현재 세션에 할당되어 있는 고유한 세션 아이디 반환
>

### void setAttribute(String name, Object value)

> 현재 세션에 value 인자로 전달되는 객체를 name 인자 이름으로 저장
>

### Object getAttribute(String name)

> 현재 세션에 value 인자로 저장되어 있는 객체 값을 찾아 반환
>

### void removeAttribute(String name)

> 현재 세션에 name 인자로 저장되어 있는 객체 값을 삭제
>

### void invalidDate()

> 현재 세션에 저장되어 있는 모든 값을 삭제
>

### 요구사항 - 1단계

1. 클라이언트와 서버 간 주고받을 고유한 아이디를 생성한다.
  - JDK에서 제공하는 UUID 클래스를 사용해 고유한 아이디를 생성할 수 있다.
2. 생성한 고유 아이디를 쿠키를 통해 전달한다.
  - 자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.
3. 서버 측에서 모든 클라이언트의 세션 값을 관리하는 저장소 클래스를 추가한다.
  - HttpSessions 클래스를 추가한다.
  - Map<String, HttpSession> 저장소를 통해 모든 클라이언트별 세션을 관리해야 한다. 키값에 고유 아이디를 전달한다.
4. 클라이언트별 세션 데이터를 관리할 수 있는 클래스를 추가한다.
  - HttpSession 클래스를 구현한다.
  - 상태 데이터를 저장할 Map<String, Object>가 필요하다.

---
# MVC 프레임워크 구현

5장에서 구현했던 WebServer 방식을 Tomcat을 사용해 구현한다.

### Controller, ~Controller

> 클라이언트 요청을 처리하는 인터페이스를 구현한다.
>
- `Controller` 클래스를 작성하고 `execute(HttpServletRequest request, HttpServletResponse response)` 메서드를 작성한다.
- 5장의 마지막에 서블릿을 사용해 리팩토링했던 ~Servlet 클래스들을 수정한다.
  - HttpServlet을 상속하던 ~Servlet 클래스들을 Controller 인터페이스를 구현하도록 변경한다.
  - 각 `~Controller` 클래스별 `execute(HttpServletRequest request, HttpServletResponse response)` 를 오버라이드하도록 하고 로직을 작성한다(~Servlet 클래스들과 동일).
- 특별한 로직 없이 리다이렉트만 담당하는 컨트롤러를 묶기 위해 `ForwardController`를 작성한다.

### RequestMapping

> 서비스에서 발생하는 모든 요청 URL과 각 URL의 비즈니스 로직을 담당할 컨트롤러를 연결한다.
>
- WAS와 동일하게 작성한 컨트롤러들을 요청 url과 매핑하는 `RequestMapping` 클래스를 작성한다.

### DispatcherServlet

> 클라이언트의 모든 요청을 받아 URL에 해당하는 컨트롤러로 작업을 위임하고 실행된 결과 페이지로 이동하는 작업을 담당한다.
>
- `WebServlet(name = “dispatcher”, urlPatterns = “/”, loadOnStartup = 1)` 어노테이션을 추가한다.
- RequestMapping을 초기화한다.
- 요청에 해당하는 Controller를 찾고 execute를 호출한다.

### ResourceFilter

> css, js 등의 요청을 처리한다.
>
- prefix를 확인해서 /css, /js 등의 리소스 파일의 경우 RequestDispatcher를 사용해 forward 요청만 보내도록 한다.

# WAS vs MVC 프레임워크

- 5장에서 Tomcat을 사용하지 않고 구현했던 web_appication_server와 MVC 프레임워크 구현 1단계를 비교한다.

## 1. 핵심 구성요소

### WAS

- WebServer
  - Socket 생성/입력 무한루프
  - 요청 발생시 RequestHandler 호출
- RequestHandler
  - HTTP 요청/헤더/본문 등 처리
  - Controller 찾아서 service 호출

### MVC

- WebServerLauncher
  - Tomcat 생성, Connector 생성, 입력 무한루프
- DispatcherServlet
  - RequestMapping 초기화
  - Controller 찾아서 execute 호출

## 2. 비교

### Request/Response

- WAS는 HttpRequest, HttpResponse 직접 생성
- MVC는 servlet의 HttpServletRequest, HttpServletResponse 사용

### HTTP Method에 따른 처리

- WAS는 요청 URL별 doGet, doPost 따로 구현
- MVC는 요청 URL별 execute로 한번에 처리