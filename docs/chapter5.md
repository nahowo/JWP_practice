# HttpRequest, HttpResponse 리팩토링
## 1. HttpRequest

### HttpRequest(InputStream in)

> InputStream을 인자로 받아 요청을 처리하는 HttpRequest 생성자이다.
>
- BufferedReader(br)를 사용해 입력 스트림에서 `requestLine`, `requestParams`, `headers`를 가져온다.
- `requestLine`
    - 입력의 첫 줄에 해당한다.
- `requestParam`
    - 빈 RequestParams 객체를 생성하고, reqeustLine에서 QueryString을 추출해 추가한다.
- `headers`
    - br에서 빈 줄이 나올 때까지 반복해서 입력받는 processHeader를 사용해 추출한다.

### createRequestLine(BufferedReader br)

> br를 사용해 입력 스트림의 첫 줄을 반환한다.
>

### processHeader(BufferedReader br)

> br로 입력받는 줄이 빈 줄이 아닌 동안 반복해서 header를 구성한다.
>

### getMethod()

> 현재 HttpRequest에서 생성된 RequestLine 인스턴스에서 메서드를 추출해 반환한다.
>

### getPath()

> 현재 HttpRequest에서 생성된 requestLine 인스턴스에서 경로를 추출해 반환한다.
>

### getHeader(String name)

> 현재 HttpRequest에서 생성된 HttpHeaders 인스턴스에서 name에 해당하는 헤더 값을 가져온다.
>

### getParameter(String name)

> 현재 HttpRequest에서 생성된 RequestParams 인스턴스에서 name에 해당하는 파라미터 값을 가져온다.
>

## 2. RequestLine

> 요청의 첫 줄에 해당한다. HTTP 메서드, 경로, 쿼리를 포함한다.
>

### RequestLine(String requestLine)

> requestLine 문자열을 인자로 받아 RequestLine 인스턴스를 생성하는 생성자이다.
>
- tokens
    - 입력받은 문자열을 공백 기준으로 나눠 HTTP 메서드, 경로(+ 쿼리)로 구분한다.
    - 경로는 “?” 기준으로 나눠 경로와 쿼리로 구분한다.

### getMethod()

> 현재 RequestLine 객체의 HTTP 메서드를 반환한다.
>

### getPath()

> 현재 RequestLine 객체의 경로를 반환한다.
>

### getQueryString()

> 현재 RequestLine 객체의 쿼리를 반환한다.
>

## 3. RequestParams

> 요청 파라미터를 Map<String, String> 형태로 가진다.
>

### addQueryString(String queryString)

> putParams를 이용해 요청 파라미터값을 추가한다.
>

### putParams(String data)

> HttpRequestUtils.parseQueryString을 이용해 data를 파싱한 후 params에 추가한다.
>

### addBody(String body)

> 입력받은 body를 putParams를 이용해 params에 추가한다.
>

### getParameter(String name)

> params에서 키가 name에 해당하는 값을 반환한다.
>

## 4. HttpHeaders

> 헤더값을 Map<String, String> 형태로 가진다.
>

### add(String header)

> 입력받은 header값을 현재 headers에 추가한다.
>
- header를 “:” 기준으로 키, 값으로 구분하고 이를 headers에 추가한다.

### getHeader(String name)

> headers에서 키가 name에 해당하는 값을 반환한다.
>

### getIntHeader(String name)

> 키에 대한 값이 정수인 경우 정수로 형변환해 반환한다.
>

### getContentLength()

> 정수로 형변환된 Content-Length의 값을 반환한다.
>

## 5. HttpResponse

> 출력 스트림 DataOutputStream, 헤더를 갖는다.
>

### HttpResponse(OutputStream out)

> out을 이용해 dos를 초기화하는 생성자이다.
>

### addHeader(String key, String value)

> key, value 쌍을 headers에 추가한다.
>

### forward(String url)

> 파일을 직접 읽어 응답으로 보내는 함수이다.
>
- 입력받은 url에 해당하는 파일을 찾아 body로 저장한다.
- contentType을 url(파일 확장자)에 따라 분류한다.
- headers에 Content-Type, Content-Length를 추가한다.
- `response200Header`로 응답한다.
- `responseBody`로 body를 출력한다.

### forwardBody(String body)

> 입력받은 body를 byte[]로 변환하고 응답 body에 추가하는 함수이다.
>
- headers에 Content-Type을 html로, Content-Length를 body 길이로 해 추가한다.
- `response200Header`로 응답한다.
- `responseBody`로 body를 출력한다.

### response200Header()

> 출력 스트림에 200 OK와 헤더를 출력하는 함수이다.
>

### responseBody(byte[] body)

> 출력 스트림에 입력받은 body를 출력하고 플러시하는 함수이다. 파일을 직접 읽는 경우(forward)에 사용한다.
>

### sendRedirect(String url)

> 다른 url로 리다이렉트하는 함수이다.
>
- 출력 스트림에 302 Found를 출력한다.
- processHeader로 헤더를 출력한다.
- Location으로 url을 출력한다.

### processHeader()

> 출력 스트림에 헤더를 {키: 값} 형태로 출력한다.
>

### 추가: forward vs forwardBody

- `forward`는 요청 경로가 기본 경로일 때 사용한다. 즉 “/”이 경로일 경우 “/index.html”로 연결되고, forward 함수를 통해 index.html 파일을 직접 읽어 응답한다.
- `forwardBody`는 요청 경로가 “/user/list”일 때 사용한다. 모든 사용자 정보를 생성해 body를 만들고, 해당 body를 responseBody를 사용해 출력 스트림에 출력한다.
- 즉 둘의 차이점은 responseBody에 들어가는 값이 파일 그 자체인지(forward), 아니면 전달되는 contents 값인지(forwardBody)에 있다.

## 6. RequestHandler 로직

- HttpRequest, HttpResponse 객체를 만들고 reqeust에서 path를 파싱한다.

### 회원가입 로직(path.equals(”/user/create”))

- 새 User 인스턴스를 만들어 request의 입력 파라미터에서 값을 가져와 아이디, 비밀번호, 이름, 이메일을 부여한다.
- 데이터베이스에 user를 추가한다.
- response에 sendRedirect로 “/index.html”을 전달한다.

### 로그인 로직(path.equals(”/user/login”))

- request의 입력 파라미터에서 값을 가져와 아이디로 User를 검색한다.
- user가 존재하지 않거나 비밀번호가 다르다면 response에 sendRedirect로 “/user/login_failed.html”을 전달한다.
- 아니라면 로그인을 진행한다.
    - **크롬으로 실습을 수행할 경우 이전 logined=true 쿠키가 남아있을 수 있다. 중복 쿠키를 방지하기 위해 request에 이미 logined=true가 존재한다면 새 쿠키를 추가하지 않는다.**
    - response 헤더에 logined=true 쿠키를 추가한다.
- response에 sendRedirect로 “/index.html”을 전달한다.

### 회원 정보 조회 로직(path.equals(”/user/list”))

- 쿠키에 logined=true가 있는지 isLogin으로 확인한다.
- 쿠키가 있다면 모든 유저 정보를 Collection으로 묶고 StringBuilder로 만들어 forwardBody에 전달한다.
- 쿠키가 없다면 response에 sendRedirect로 “/user/login.html”을 전달한다.

### 기본 경로 로직

- forward로 “/index.html”을 전달한다.

---
# 웹 서버 리팩토링
## 1. Controller

> service를 공통으로 처리하는 `Controller` 인터페이스를 작성한다.
>
- 모든 요청 url에서 HttpRequest, HttpResponse를 입력받기 때문에 해당 부분을 받아 처리하는 service 메서드를 작성한다.

## 2. AbstractController

> Controller를 구현하는 `AbstractController` 추상 클래스를 작성한다.
>
- 메서드별 doGet/doPost를 실행하는 service를 작성한다. Controller의 service를 오버라이딩한다.

## 3. 개별 요청별 Controller

> 회원가입, 로그인, 조회 별 AbstractController를 상속하는 `{메서드}Controller` 클래스들을 작성한다.
>
- 요청 url의 타입에 따라 doPost, doGet을 작성한다. AbstractController의 doPost, doGet을 오버라이딩하도록 하고 로직을 추가한다.

![IMG_2557](https://github.com/user-attachments/assets/a3499d61-3715-446e-aa3e-8c978e3469db)


## 로직

1. 요청이 발생하면 RequestHandler가 RequestMapping을 사용해 요청에 해당하는 특정 Controller를 생성한다.
2. 생성된 Controller에서 service를 호출한다.
3. Controller에서 service가 호출된다.
4. AbstractController에서 오버라이드한 service가 호출된다.
5. 요청의 메서드에 따라 doGet/doPost가 호출된다.
6. 생성된 클래스에서 오버라이드한 doGet/doPost가 호출된다.
7. 로직이 수행된다.
