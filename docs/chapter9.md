# 자체 점검 요구사항(필수)

## 1. Tomcat 서버 시작 시 서블릿 컨테이너의 초기화 과정

> ContextLoaderListener와 DispatcherServlet 클래스부터 시작한다.
>

### Context

- Tomcat에서 context란 웹 애플리케이션을 의미한다.

### @WebListener - ContextLoaderListener

> 해당 클래스가 WebListener임을 선언한다. 이때 클래스는 [`ServletContextListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/ServletContextListener.html), [`ServletContextAttributeListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/ServletContextAttributeListener.html), [`ServletRequestListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/ServletRequestListener.html), [`ServletRequestAttributeListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/ServletRequestAttributeListener.html), [`HttpSessionListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpSessionListener.html),  [`HttpSessionAttributeListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpSessionAttributeListener.html), [`HttpSessionIdListener`](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpSessionIdListener.html)중 하나 이상을 구현해야 한다.
>
- ServletContextListener를 상속하는 ContextLoaderListener의 `contextInitialized` 메서드가 실행된다.
- data.sql을 실행해 테이블과 목업 데이터를 생성한다.
    - 현재 drop table if exists가 되어 있어 매번 새 테이블을 생성한다.

### @WebServlet - DispatcherServlet

> 해당 클래스가 servlet임을 선언한다. 컨테이너에 의해 수행된다.
>
- servlet 이름이 ‘dispatcher’이고, 기본 urlPattern이 ‘/’이고, load on startup 순서가 1로 정의되어 있다.
- HttpServlet을 상속하는 DispatcherServlet의 `init` 메서드가 실행된다.
    - RequestMapping을 초기화한다.
- 클라이언트의 요청이 발생할 때마다 DispatcherServlet의 `service` 메서드가 실행된다.
    - requestMapping 인스턴스에서 요청 url과 매칭되는 컨트롤러를 찾고, 해당 컨트롤러의 `execute` 메서드를 실행한다.
    - 결과 ModelAndView에서 `getView` 메서드로 View를 반환하고, 해당 View에서 다시 `render` 메서드를 통해 결과를 출력한다(json/jsp).

## 2. Tomcat 서버 시작 이후 클라이언트 요청-페이지 응답까지의 과정

### WebServerLauncher

- WebServerLauncher의 `main` 메서드가 실행된다.
    - war 폴더가 복사될 기본 디렉토리를 webapp/으로 설정한다. 프로젝트 루트 디렉토리/webapp 디렉토리에 war 폴더가 복사됨을 의미한다.
    - Tomcat 인스턴스를 생성한다.
    - 포트를 8080으로 설정한다.
    - 지정했던 기본 디렉토리로 war 폴더가 복사된다.
    - tomcat을 시작한다.
    - tomcat의 기본 커넥터를 가져온다.
    - tomcat 종료 명령이 들어올 때까지 대기한다.
- 서블릿 컨테이너가 초기화된다.
- http://localhost:8080/로 클라이언트 요청 발생 시
    - tomcat의 connector가 HTTP 요청을 읽는다.
    - DispatcherServlet의 `service` 메서드를 실행한다.
        - requestMapping에 ‘/’는 HomeController 인스턴스로 설정되어 있으므로 HomeController.`execute`를 실행한다.
            - homeController가 생성될 때 생성된 questionDao에서 `findAll` 메서드를 통해 모든 질문 튜플을 가져온다.
            - jspView로 url은 ‘home.jsp’를 전달한다.
            - ModelAndView의 `addObject`메서드로 model에 질문 튜플을 추가한다.
        - 반환된 modelAndView에서 view를 반환한다.
        - view에서 render 메서드를 실행해 request attribute에 model의 각 데이터를 추가한다.
        - ‘home.jsp’에 전달된 attribute를 매칭해 클라이언트에게 전달한다.

## 3. 질문 추가 기능 구현

- AddQuestionController를 추가한다.
    - ‘/’로 이동하는 JspView를 사용한다.
- QuestionDao에 insert 메서드를 추가한다.
- RequestMapping의 ‘api/qna/addQuestion’에 AddQuestionController를 연결한다.

## 4. 질문 사용자 수정

> 로그인한 사용자만 질문할 수 있도록 수정한다.
작성자를 입력하지 않고 로그인된 사용자 정보로 질문을 생성한다.
>
- form.jsp에서 글쓴이 폼을 제거한다.
- CreateQuestionController 수정
    - 세션에서 유저 정보를 가져오고 로그인되지 않았다면 로그인 페이지로 리다이렉트한다.
    - 로그인되었다면 writer를 userId로 지정하고 기존 로직을 수행한다.
- QuestionFormController 생성
    - /qna/form에서 바로 Forward하지 않고 로그인 여부를 검증한 뒤 요청을 보내기 위해 추가한다.
    - 세션에서 유저정보를 가져와 로그인하지 않았다면 로그인 페이지로 리다이렉트한다.
    - 로그인되었다면 form.jsp 파일을 보여준다.

## 5. 질문 상세보기 화면 이동

- 완료

## 6. 한글 깨짐 해결

- ResourceFilter처럼 모든 요청에 대해 UTF-8 인코딩을 적용하는 필터를 작성한다.
- @WebFilter(”/*) 어노테이션을 적용해서 필터가 모든 요청에 적용되도록 한다.

## 7. ShowController 멀티스레드 문제 발생 이유/해결 - TODO

- 기존 코드는 다음과 같다.

    ```java
    public class ShowController extends AbstractController {
        private QuestionDao questionDao = new QuestionDao();
        private AnswerDao answerDao = new AnswerDao();
        private Question question;
        private List<Answer> answers;
    
        @Override
        public ModelAndView execute(HttpServletRequest req, HttpServletResponse response) throws Exception {
            Long questionId = Long.parseLong(req.getParameter("questionId"));
    
            question = questionDao.findById(questionId);
            answers = answerDao.findAllByQuestionId(questionId);
    
            ModelAndView mav = jspView("/qna/show.jsp");
            mav.addObject("question", question);
            mav.addObject("answers", answers);
            return mav;
        }
    }
    ```

    - question, answers가 멤버 변수로 설정되어 있다.


## 8. 답변 추가/삭제 시 countOfAnswers 변경

- 답변 추가
    - AnswerDao에서 insert()시 questionDao의 update() 호출
        - 현재 값 +1로 업데이트
- 답변 삭제
    - AnswerDao에서 delete()시 questionDao의 update() 호출
        - 현재 값 -1로 업데이트

## 9. 질문 목록을 json으로 반환하는 API 추가

> /api/qna/list로 접근 시 질문 목록을 json으로 반환하도록 한다.
>
- 각 질문을 question으로 묶고 나열해 반환한다.
- questionDao에서 `findAll` 한 결과를 List로 저장한 뒤 addObject로 전달한다.

## 10. 상세보기 화면의 답변 목록에서 답변 삭제(AJAX)

- 완료

## 11. 질문 수정

> 질문 수정은 글쓴이와 로그인된 사용자가 같은 경우 수정이 가능하다.
>
- show.jsp의 수정/삭제 버튼
    - 로그인된 사용자가 글쓴이와 같을 때만 나타나도록 수정한다.
- UpdateFormQuestionController
    - 사용자가 로그인하지 않았다면 오류를 반환한다.
    - 로그인된 사용자와 글쓴이가 다르면 오류를 반환한다.
    - parameter로 questionId를 담아 /qna/updatedForm.jsp로 리다이렉트한다.
- UpdateQuestionController
    - 사용자가 로그인하지 않았다면 오류를 반환한다.
    - 로그인된 사용자와 글쓴이가 다르면 오류를 반환한다.
    - QuestionDao를 사용해 업데이트한다.
    - parameter로 questionId={questionId}를 담아 리다이렉트한다.

## 12. DAO의 JdbcTemplate 인스턴스를 하나로 통합

### 싱글턴 패턴

- 싱글턴 패턴이란 한 클래스가 하나의 인스턴스만 가지도록 하는 것을 의미한다. Jdbc 커넥션 등에서 사용한다. 필요없는 자원 낭비를 줄일 수 있다.

## 13. 질문 삭제

> 답변이 없는 경우/질문자와 답변자가 모두 같은 경우 질문 삭제가 가능하다.
>
- 답변 개수를 불러온 뒤 개수 확인
- 웹 브라우저(”/qna/answerDelete”)
    - JspView를 사용해 메인으로 리다이렉트
- 모바일 앱(”/api/qna/answerDelete”)
    - JsonView를 사용해 응답 결과를 json으로 전송
- 중복을 제거하기 위해 새로운 계층 추가(service)
    - 답변 개수 확인 메서드
    - 답변 작성자 확인 메서드
    - 사용자/작성자 확인 메서드

# 자체 점검 요구사항(선택)

## 14. 단위 테스트

> DI, Map을 사용한 메모리 DB, Mockito 사용
>

## 15. 컨트롤러 자동 매핑

> @Controller, 자바 리플렉션 활용
>
- [자바 강의](https://github.com/nahowo/java-lecture/blob/main/source/src/java_adv2/README.md#%EC%84%B9%EC%85%98-13-%EB%A6%AC%ED%94%8C%EB%A0%89%EC%85%98)에서 실습했던 내용을 바탕으로 컨트롤러 매핑을 수행하기로 했다.
- 직접 @Mapping 어노테이션을 만들어서 적용한다. 

### @Mapping 어노테이션 작성

- String value()를 가지는 어노테이션 인터페이스를 작성한다.
- @Retention(RetentionPolicy.RUNTIME)을 통해 런타임에도 어노테이션이 남아있도록 설정한다.
- @Target(ElementType.METHOD)를 통해 어노테이션이 메서드에 적용됨을 명시한다.

### @Mapping을 처리하는 로직 작성 - RequestMapping에 추가

- 작성하기 이전에 컨트롤러가 기능별로 분산되어 있어 도메인 별로 통합하는 것이 필요하다고 생각했다.
  - 기존 방식은 기능별 컨트롤러 클래스를 작성하고, 각 클래스의 execute() 메서드를 호출하는 방식이다.
  - 자바 강의에서 실습했던 내용은 도메인 별 클래스를 작성하고, 각 메서드를 url에 매핑해 사용하는 방식이다.
  - 후자가 코드 중복도 줄이고 리플렉션을 제대로 활용하는 방식이라고 생각해 선택했다.

### 컨트롤러 수정

- 도메인마다 하나의 컨트롤러를 가지고, `execute()` 메서드 대신 기능별 메서드를 가지도록 수정했다.
- execute() 메서드의 파라미터도 request, response를 고정으로 가지지 않고 필요한 파라미터만 가지도록 수정했다(동적 바인딩 참고).
- 더이상 Controller 인터페이스의 `execute()` 메서드를 오버라이드하지 않기 때문에 제거했다.

### DispatcherServlet 초기화 로직 수정

- 기존의 컨트롤러 수동 등록 로직을 수행하던 DispatcherServlet의 `init()` 메서드를 수정한다.
  - RequestMapping의 `initMapping()` 메서드에서 단순히 Map에 url과 컨트롤러를 직접 작성하는 게 아니라 리플렉션을 이용해 컨트롤러 메서드와 url을 매핑하도록 변경한다.

### DispatcherServlet의 컨트롤러 호출 방식 수정

- 기존 방식은 RequestMapping에서 매칭되는 컨트롤러를 찾고 그 컨트롤러의 `execute()` 메서드를 호출하는 방식이다.
- 기존 방식의 문제점은 `execute()` 메서드가 Controller 인터페이스의 구현으로 오버라이드하기 때문에 파라미터가 고정되고(필요하지 않은 request, response를 전달), 각 기능별 컨트롤러를 작성해야 한다는 것이다.
- 1번 문제를 해결하기 위해 메서드별 인자 동적 바인딩을 사용할 수 있다.
- 2번 문제를 해결하기 위해 도메인별 컨트롤러를 작성하고, url에 컨트롤러가 아닌 메서드 + 컨트롤러 객체인 ControllerMethod를 매칭하도록 할 수 있다.

### Method.invoke()의 반환값 관련 문제

- 기존의 MVC 패턴에서 메서드 반환값을 ModelAndView로 통일했다. Method.invoke()의 반환값은 Object이기 때문에 DispatcherServlet에서 처리하기 위해서는 (ModelAndView) 를 사용해 반환값을 형변환해야 한다.

---

# 트러블슈팅

### org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: Unique index or primary key violation

- 목업 데이터 생성 시 pk값을 명시하면 생기는 문제이다. 하나라도 pk값을 명시하면 오류가 생긴다.

### json 구조 해결하기

- 이전 해결했던 방식으로 {answers: }로 한번 묶지 않고 데이터를 전송하니까 여러 데이터를 전송할 때 구조가 깨지는 문제가 발생했다.
- 따라서 구조는 아래처럼 다시 변경했다.

    ```json
    {"answers":
    	[
    		{
    			"answerId": 1,
    			"contents": "hello",
    			"questionId": 3,
    			"writer": "nahowo"
    		}
    	]
    }
    ```

- 각 answer를 배열로 나열하도록 변경했다.
- 대신 ajax 스크립트에서 addAnswer, deleteAnswer의 경우 한 가지 answer만 다루게 되므로 json.answer대신 json.answer[0]을 사용하도록 변경했다.