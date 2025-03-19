## 요구사항 1 - index.html 응답하기

> InputStream을 한 줄 단위로 읽기 위해 BufferedReader를 생성한다.
BufferedReader.readLine()으로 라인별 HTTP 요청 정보를 읽는다.
HTTP 요청 정보 전체를 출력한다.
>

### BufferedReader 인스턴스 만들기

- Reader 확장
- 문자 input stream, 문자열에서 텍스트를 읽어 문자열을 효율적으로 읽을 수 있도록 한다.
- 매개변수로 Reader를 받는다.
- InputStream을 BufferedReader로 읽기 위해 InputStreamReader를 생성하고 인수로 전달했다.

### 라인별 요청 정보 출력하기

- log.info로 각 line을 출력했다.
- 각 라인별 숫자를 부여하고 1번째 라인(GET /index.html HTTP/1.1)을 파싱하도록 했다.

### url 파싱하기

- splitUrl 메서드를 만들어 라인을 파싱하고 요청 Url을 전달하도록 했다.

---

### 리팩토링

- 1번째 라인에서만 url을 파싱하기 때문에 while문을 들어가기 전에 url을 파싱하고, 나머지 라인들은 라인별 숫자 없이 while문으로 처리했다.

  → 메서드 복잡도를 12에서 8로 줄일 수 있었다.


## 요구사항 2 - GET 방식으로 회원가입하기

> HTTP 요청의 첫 번째 라인에서 요청 URL을 추출한다.
요청 URL에서 접근 경로와 `이름=값`으로 전달되는 데이터를 추출해 User 클래스에 담는다.
`이름=값` 파싱은 util.HttpRequestUtils 클래스의 parseQueryString() 메서드를 사용한다.
요청 URL과 이름 값을 분리해야 한다.
>

### 요청 url 추출

- HTTP 요청은 다음과 같다.

    ```java
    user/create?userId=nahowo&password=1234&name=nahyun+park&email=nahowo%40naver.com
    ```

- 현재 코드로 회원가입 폼을 작성해 요청하면 아래와 같은 오류 메시지가 발생한다.

    ```java
    [Thread-17] ERROR w.webserver.RequestHandler - ./webapp/user/create?userId=nahowo&password=1234&name=nahyun+park&email=nahowo%40naver.com
    ```

- 요청 url과 파라미터를 “?” 기준으로 추출한다.

### User 클래스

```java
package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
```

## 요구사항 3 - POST 방식으로 회원가입하기

> form.html 파일의 form 태그 method를 get에서 post로 수정한 후 회원가입이 정상적으로 동작하도록 구현한다.
>
- POST로 데이터를 전달할 경우 전달하는 데이터는 HTTP 본문(body)에 담긴다.
- HTTP 본문은 HTTP 헤더 이후 빈 공백을 가지는 한 줄 다음부터 시작한다.
- HTTP 본문에 전달되는 데이터는 GET 방식으로 데이터를 전달할 때의 `이름=값`과 같다.
- BufferedReader에서 본문 데이터는 util.IOUtils 클래스의 readData() 메서드를 활용한다. 본문의 길이는 HTTP 헤더의 Content-Length 값이다.
- 회원가입시 입력한 모든 데이터를 추출해 User 객체를 생성한다.

### POST 방식으로 변경

- 메서드를 get에서 post로 변경한 뒤 요청하면 아래와 같은 오류가 발생한다.

    ```java
    20:56:19.770 [Thread-12] ERROR w.webserver.RequestHandler - ./webapp/user/create
    ```

- 해당 오류는 /webapp 디렉토리에 user/create 라는 파일이 존재하지 않기 때문에 발생한다.
- html form 태그를 get 대신 post로 변경하면 폼 작성 후 요청 시 GET 대신 POST 요청이 발생한다.

### 요청 데이터 파싱

- IOUtils.readData() 메서드를 이용해 br에서 데이터를 읽어온다.

## 요구사항 4 - 302 status code 적용하기

> 회원가입 완료 시 /index.html 페이지로 이동하도록 한다.
>
- HTTP 응답 헤더의 status code를 200이 아니라 302로 변경한다.
- response302Header() 메서드를 생성해서 해당 메서드를 사용하도록 한다.

## 요구사항 5 - 로그인하기

> 로그인 메뉴를 클릭하면 /login.html로 이동해 로그인할 수 있다. 로그인이 성공하면 index.html로 이동하고, 로그인이 실패하면 login_failed.html로 이동해야 한다.
>
- 회원가입한 사용자로 로그인할 수 있어야 한다.
- 로그인이 성공하면 쿠키를 활용해 로그인 상태를 유지할 수 있어야 한다.
- 로그인에 성공할 경우 요청 헤더의 Cookie 헤더 값이 logined=true, 로그인에 실패할 경우 logined=false로 전달되어야 한다.

### 로그인 성공 시: HTTP 헤더에 쿠키 추가하기

- 응답 헤더가 아래와 같아야 한다.

    ```java
    HTTP/1.1 200 OK
    Content-Type: text/html
    Set-Cookie: logined=true
    ```

- 요청은 아래와 같다.

    ```java
    request line: GET /user/user/login.html HTTP/1.1
    request line: POST /user/create HTTP/1.1
    ```


## 요구사항 6 - 사용자 목록 출력하기

> 사용자가 로그인 상태일 경우 user/list로 접근했을 때 사용자 목록을 출력한다. 만약 로그인하지 않은 상태라면 로그인 페이지로 이동한다.
>
- 로그인 여부 판단을 위한 Cookie 파싱은 util.HttpRequestUtils 클래스의 parseCookies() 메서드를 사용한다.
- StringBuilder를 활용해 사용자 목록을 출력하는 HTML을 동적으로 생성한 후 응답으로 보낸다.

## 요구사항 7 - CSS 지원하기

> 소스코드가 CSS 파일을 지원하도록 구현한다.
>
- Content-Type 헤더 값을 text/html이 아니라 text/css로 응답하도록 수정한다.

---

# 응답 메시지

### 200 Ok

- 서버가 요청을 제대로 처리했다는 뜻이다. 서버가 요청한 페이지를 제공했다는 의미로 쓰인다.

### 302 Found

- URL 리다이렉션을 수행하는 일반적인 방법이다.
- 302 HTTP 응답은 위치 헤더 필드(Location)에 URL을 추가로 제공한다. 이는 위치 필드에 저장된 새 URL에 대해 동일한 두 번째 요청을 생성하도록 사용자 에이전트(예: 웹 브라우저)에 대한 초대이다. 최종 결과는 새 URL로 리다이렉션된다.

## 코드 분석

```java
private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css \r\n");
            dos.writeBytes("Content-Length" + lengthOfBodyContent + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void responseLogin302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lenghtOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 ok \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-length: " + lenghtOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
```

## OutputStream vs DataOutputStream

### OutputStream

> https://docs.oracle.com/javase/8/docs/api/java/io/OutputStream.html
>
- Object 상속
- Closeable, Flushable 구현
- 모든 바이트 출력 스트림 클래스의 최상위 추상 클래스이다. 단순 바이트 단위 데이터 출력 기능을 제공한다.

### DataOutPutStream

> https://docs.oracle.com/javase/8/docs/api/java/io/DataOutputStream.html
>
- FilterOutputStream 상속
- DataOutput 구현
- 기본 타입 데이터들을 출력하기 위한 데이터 출력 스트림이다. portable하기 때문에 애플리케이션은 데이터 입력 스트림으로 출력한 데이터를 다시 읽을 수 있다.
- OutPutStream을 확장한 보조 스트림(decorator)이다.
- `writeBytes(String s)`
    - s에서 바이트를 작성한다.
- `write(byte[] b, int off, int len)`
    - b에서 off만큼의 오프셋에서부터 len개의 바이트를 작성한다.
- `flush()`
    - 데이터 출력 스트림을 플러시한다.

### 차이점

- dos는 os와 다르게 다양한 기본 데이터 타입의 데이터를 바이트 변환 없이 바로 쓸 수 있다. os는 바이트 단위로 출력하고, 기본 데이터 타입을 출력하기 위해서는 직접 바이트 배열로 변환해야 한다.

## 응답 메시지 분석

### response200CssHeader

- 200 Ok
- Content-Type: text/css
- Content-Length: {body.length}

### response200Header

- 200 Ok
- Content-Type: text/html;charset=utf-8
- Content-Length: {body.length}

### responseLogin302Header

- 302 Redirect
- Set-Cookie: logined=true
- Location: /index.html

### response302Header

- 302 Redirect
- Location: {url}

### responseResource

> 정적 파일을 읽어들이고 반환한다. 200 Ok header와 body를 출력한다.
>

### responseBody

> body를 출력하고 출력 스트림을 플러시한다.
>

# 리팩토링

## response header

### response200Header

- response200Header와 response200CssHeader를 하나로 합친다.
- String contentType을 매개변수로 설정해서 중복 코드를 줄일 수 있다.
- css가 필요한 부분은 요청 url이 .css로 끝나는 경우이다.

### response302Header

- response302Header와 responseLogin302Header를 하나로 합친다.
- Set-Cookie를 매개변수로 설정하고 null인 경우 추가하지 않도록 해서 중복 코드를 줄일 수 있다.

```java
private void response200Header(DataOutputStream dos, int lenghtOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 ok \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-length: " + lenghtOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url, String cookies) {
        try {
            dos.writeBytes("HTTP/1.1 302 redirect \r\n");
            if (cookies != null) {
                dos.writeBytes("Set-Cookie: " + cookies + " \r\n");
            }
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length, "text/html;charset=utf-8");
        responseBody(dos, body);
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
```

## run()

```java
if ("/user/create".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                User user = signUp(body);
                log.debug("user: " + user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html", null);

                DataBase.addUser(user);

            } else if ("/user/login".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> param = getBodyParam(body);
                User user = DataBase.findUserById(param.get("userId"));
                log.debug("logined user: " + user);
                if (user == null) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }
                if (user.getPassword().equals(param.get("password"))) {
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, "/index.html", "logined=true");
                } else {
                    responseResource(out, "/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {
                if (!logined) {
                    responseResource(out, "/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                byte[] body = userStringBuilder(users);
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length, "text/html;charset=utf-8");
                responseBody(dos, body);
            } else if (url.endsWith(".css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length, "text/css");
                responseBody(dos, body);
            } else {
                responseResource(out, url);
            }
```

### /user/create

- readData로 body 읽어오기
- User 객체 생성
- Database.addUser()
- response302Header
    - dos
    - url: “/index.html”
    - cookies: null

### /user/login

- readData로 body 읽어오기
- getBodyParam으로 유저정보 읽어오기
- Database.findUserById()
- 유저정보로 로그인 가능 여부 판단
    1. user가 null인 경우:
        - responseResource
            - url: “/user/login_failed.html”
    2. user.getPassword()가 param의 password와 다른 경우:
        - 위와 동일
    3. user.getPassword()가 param의 password와 같은 경우:
        - response302Header
            - url: “/index.html”
            - cookies: “logined=true”

### /user/list

- logined가 false인 경우:
    - responseResource
        - url: “/user/login.html”
- 아닌 경우:
    - 사용자 목록 body 생성
    - response200Header
        - contentType: “text/html”
    - responseBody

### .css

- response200Header
    - readData로 body 읽어오기
    - contentType: “text/css”
    - responseBody

### 나머지 경우

- responseResource