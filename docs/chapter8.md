user 로직을 구현했던 방식을 복습하며 질문/답변 게시판을 구현해 보자.

# 요구사항 분석: 질문/답변 조회

- home.jsp에서 실제 작성된 질문을 조회할 수 있도록 구현한다.
- home.jsp에서 개별 질문에 접근하면 질문의 내용과 답변 내용을 볼 수 있도록 한다.

---

# AJAX를 활용한 답변 추가/삭제

## AJAX

> AJAX(Asynchronous JavaScript and XML)는 비동기적인 웹 애플리케이션의 제작을 위해 아래와 같은 조합을 이용하는 웹 개발 기법이다.
1. 표현 정보를 위한 HTML과 CSS
2. 동적 화면 출력 및 표시 정보와의 상호작용을 위한 DOM, 자바스크립트
3. 웹 서버와 비동기적으로 데이터를 교환하고 조작하기 위한 XML, XSLT, XMLHttpRequest
- 브라우저가 서버에서 HTML 응답을 받아 처리하는 과정은 다음과 같다.
    - HTML 응답을 받으면 브라우저는 HTML을 라인 단위로 읽어내려가면서 서버에 재요청이 필요한 부분(CSS, 자바스크립트, 이미지 등)을 찾아 서버에 다시 요청을 보낸다.
    - 서버에서 자원을 다운로드하면서 HTML DOM 트리를 구성한다.
    - 서버에서 CSS 파일을 다운로드하면 앞에서 생성한 HTML DOM 트리에 CSS 스타일을 적용한 후 모니터 화면에 그리게 된다.
- 이와 같이 서버에 요청-응답을 통해 사용자에게 화면을 보여주기까지 많은 단계와 비용이 발생한다.
- 답변 추가/삭제의 경우 화면 대부분을 변경할 필요 없이 추가/삭제되는 영역만 처리하면 된다. 이를 위해 AJAX를 사용한다.

# 설계

## 1. 도메인(모델) 작성

### Question

- long `questionId`
- String `writer`
- String `title`
- String `contents`
- Datetime `createdDate`
- int `countOfAnswer`

### Answer

- long `answerId`
- String `writer`
- String `contents`
- Datetime `createdDate`
- long `questionId`

## 2. Controller 작성

> Controller 인터페이스의 execute 메서드를 구현하는 각 로직 구현
>

### AddAnswerController

- request에서 Answer 데이터로 Answer 도메인 생성
- AnswerDao.insert(Answer)로 데이터 저장
- null 반환

### DeleteAnswerController

- request에서 answerId 추출
- AnswerDao.delete(answerId)로 데이터 삭제
- null 반환

## 3. DAO 작성

### AnswerDao

- JdbcTemplate을 이용해 insert, delete 수행

## 4. RequestMapping으로 Controller 연결

- /api/qna/addAnswer → AddAnswerController
- /api/qna/deleteAnswer → DeleteAnswerController

---

# 트러블슈팅

### js 파일 수정 적용 안되는 문제

- js 파일을 수정했는데도 크롬에서 새 요청을 보내면 동일한 오류가 발생하고, 오류가 발생한 js 파일을 열어 보니 수정 이전의 파일을 확인할 수 있었다.
- 크롬이 js 파일을 캐싱해서 수정사항이 적용되지 않는 문제로, 개발자 도구 > Networks > disable cache를 체크해 캐싱을 제거할 수 있다. 다만 이 경우 모든 웹에서 캐시 사용을 제거하기 때문에 개발 이외 상황에도 영향을 줄 수 있다.
- disable cache 대신 새로고침 시 js 파일도 새로고침하려면 새로고침 버튼에서 ‘캐시 비우기 및 강력 새로고침’을 선택하면 된다.

### AJAX script 불러오지 못하는 문제

- jsp 파일에서 `<script>` 태그를 사용해 src 속성에 js 파일 경로를 지정할 수 있다. 이때 상대경로를 사용하며 경로에 오류가 있을 경우 스크립트가 로드되지 않는다.
- jsp element가 전부 로드되기 전에 스크립트가 실행되면 안 되므로 `<script>` 태그는 <body>의 가장 마지막에 위치시킨다.

### ResultSet.next()가 false인 문제

- AnswerDao에서 새 Answer를 생성하고, 새로 생성한 데이터를 받아오기 위해 `ResultSet`에 `PreparedStatement.getGeneratedKey()` 메서드를 사용한다.
- 이 때 `PreparedStatement`를 생성할 때 `Statement.RETURN_GENERATED_KEYS` 설정을 하지 않으면 ResultSet.next()가 false가 되어 생성된 튜플의 auto_increment 값을 불러올 수 없다.

---
### 현재까지의 문제점

1. JSON 응답 시 이동할 JSP 페이지가 없어 null을 반환하고 있다.
2. 자바 객체를 JSON으로 변환/응답 부분이 중복되고 있다.

# JSON 응답 처리하기

```java
@Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();
        log.debug("Method: {}, RequestURI: {}", request.getMethod(), requestUrl);

        Controller controller = rm.findController(requestUrl);
        try {
            String viewName = controller.execute(request, response);
            if (viewName != null) {
                move(viewName, request, response);
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
    }
```

- 현재 Controller는 jsp에 대한 처리만을 수행하고 있다. json 데이터를 반환하는 경우에는 null로 처리하게 된다.
- Controller의 execute 메서드 반환 타입을 String 대신 jsp, json으로 나누어 반환해보자.

## 반환 타입별 View 생성하기

### Controller 반환 타입 생성: View

- jsp, json, 이후 추가될 view(excel, PDF 등)를 처리하기 위해 DispatcherServlet에서 분기 없이 처리할 수 있도록 새로운 반환 타입을 정의한다.

### View 인터페이스

- view 타입별 작업을 수행할 `render` 메서드를 정의한다.

### JspView 클래스

- View view = controller.execute(request, response)가 된다.
- DispatcherServlet의 move() 메서드처럼 각 view 타입별 작업을 수행할 함수를 작성한다.
- render 메서드를 구현한다.
    - jsp에서 사용하는 데이터를 request의 Attribute로 추가하고 url을 반환한다.

### JsonView 클래스

- 동일하게 View view = controller.execute(request, response)가 된다.
- render 메서드를 구현한다.
    - ObjectMapper를 이용해 자바 객체를 json 형식으로 전환한다.

## Controller 반환값 수정하기

- jsp 파일을 반환하는 컨트롤러는 jspView를 반환하도록, json 데이터를 반환하는 컨트롤러는 jsonView를 반환하도록 수정한다.
- DispatcherServlet은 이제 컨트롤러에서 execute를 수행한 뒤 반환값을 View로 저장하고, View.render()를 수행하기만 하면 된다.

---

# 트러블슈팅

### /api/qna/addAnswer 데이터를 {1} 형식으로 출력하는 문제

- 데이터베이스에는 정상 반영되는데 화면상 실제 데이터가 아니라 {1}, {2}, … 형식으로 출력되는 문제가 발생했다.
- 정상 작동하던 이전 코드

  ![image.png](attachment:c1eed003-e33d-4bd5-aaa9-997fd9c440cc:image.png)

- 오류가 발생한 코드

  ![image.png](attachment:9ebc9299-63de-48e3-be6a-952c3d25425e:image.png)

- answer안에 answer가 중첩으로 들어간다.
- 중첩을 해결하기 위해 JsonView의 render() 함수를 수정했다.
    - 기존 함수

        ```java
        public class JsonView implements View {
            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
                ObjectMapper mapper = new ObjectMapper();
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print(mapper.writeValueAsString(model));
            }
        }
        ```

        - model에 {”answerId”: 6, “writer”: “nahyun park”, … } 형태의 Map<String, Object>가 들어간다.  이걸 그대로 out에 추가하면 “answer”: {”answerId”: 6, “writer”: “nahyun park”, … }} 형식으로 중첩이 적용된다.
    - 해결을 위해 바로 model을 추가하는 게 아니라 model의 키값을 순회하며 json을 구성하도록 변경했다.
    - 변경한 함수

        ```java
        public class JsonView implements View {
            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
                ObjectMapper mapper = new ObjectMapper();
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                for (String key : model.keySet()) {
                    out.print(mapper.writeValueAsString(model.get(key)));
                }
            }
        }
        ```

    - 결과

      ![image.png](attachment:a8a9ae31-fe84-486f-a5cf-163570d5fc46:image.png)


### /api/qna/deleteAnswer AJAX 오류 문제

- addAnswer과 동일하게 데이터베이스에서는 삭제되는데 error alert가 뜨고 새로고침 전에는 화면이 변하지 않는 문제가 발생했다.
- 기존 결과

  ![image.png](attachment:6f2ce6c1-f769-4442-890f-1ff557983e1a:image.png)

- 동일한 json 중첩 문제가 발생했고 동일한 해결 방법으로 해결했다.

## 고민사항

- JSON 매핑 시 중첩해서 사용해야 하는지?
    - 기존에는 데이터 이름 없이 그냥 {att1: attVal1, att2: attVal2, … } 형식의 json을 사용했는데, OutputBuffer를 JsonView 클래스의 render 메서드에서 수행하도록 리팩토링하는 과정에서 {data: {att1: attVal1, att2: attVal2, … }} 형식의 model을 사용하게 되었다.
    - 과연 {data: {att1: attVal1, att2: attVal2, … }}의 data를 사용해야 할 필요가 있을까?
    - 어차피 현재로서는 json 매핑 오류를 해결하기 위해 전자의 json 형식을 사용하고 있다.
    - 현재 ModelAndView의 model은 {data: {att1: attVal1, att2: attVal2, … }} 형식의 Map<String, Object> 변수인데, 어차피 data값을 사용하지 않으므로 굳이 Map 형식을 사용하지 않아도 된다. 그냥 List<Object>로 만들어도 된다.