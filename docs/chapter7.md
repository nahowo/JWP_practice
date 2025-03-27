- 자바 진영은 JDBC 표준을 통해 데이터베이스와의 통신을 담당하도록 지원한다.
- JDK에서 제공하는 java.sql 패키지의 JDBC 소스코드는 인터페이스만 정의해 제공하고 있다.
- 이처럼 JDBC는 데이터베이스 통신을 위한 규약만 정하고 이에 대한 구현체는 데이터베이스를 만들어 서비스하는 회사가 제공하도록 하고 있다.

# 회원 데이터를 DB에 저장하기

## 1. H2 DB 설치

- DB 설치 없이 경량 DB인 H2를 사용해 실습을 진행한다.
- ./main/resources 디렉토리에 data.sql을 작성한다. 테이블, 목업 데이터를 작성하면 된다.
- [링크](https://www.h2database.com/html/download.html)에서 jar 파일을 받고, Tomcat의 base directory 내부의 libs 폴더에 붙여넣는다.

### 내장 H2 데이터베이스 intelliJ와 연결하기

1. 데이터베이스 연결 추가에서 H2 선택
2. URL을 서버에서 사용하는 것과 동일하게 설정
3. Auth는 User에 sa, 비밀번호는 빈 문자열로 설정(`contextInitialized`와 동일)
4. Driver가 없다면 다운로드
- H2 연결 방법에는 3가지가 있다. ([참고링크](https://m-falcon.tistory.com/710))
    1. Embedded Mode: JVM 내에 DB를 내장한다. 외부 클라이언트 접근이 불가능하다.
    2. Server-Client Mode: H2 서버에 DB가 존재한다. TCP/IP 네트워크 IO로 데이터 통신이 이루어지도록 하고 외부 접근이 가능하다.
    3. Mixed Mode: 최초로 DB를 연결한 앱이 자체 H2 서버를 띄워 DB를 래핑한다. 최초 연결은 Embedded 방식으로, 다른 외부 앱은 동일 URL로 접근이 가능하다.
- 로컬 환경이지만 intelliJ IDEA에서 연결하는 등 실제 DB를 볼 수 있도록 하기 위해서는 Mixed Mode를 사용한다. 이 때 url의 끝에 `;AUTO_SERVER=TRUE`를 지정해줘야 한다.

### ContextLoaderListener 작성하기

- 서버 시작 시 data.sql을 실행하기 위해 `WebListener`를 작성한다.
- WebListener로 선언하고 ServletContextListener를 구현하는 ContextLoaderListener를 작성한다.
- ServletContextListener 
  - ServletContext 라이프사이클 변화 이벤트를 수신하기 위한 인터페이스이다. 

- `contextInitialized`, `contextDestroyed`를 오버라이드하고 data.sql을 실행한다.
- 이제 서버를 실행하면 data.sql에서 작성했던 테이블과 목업 데이터가 DB에 존재하는 것을 intelliJ로 확인할 수 있다.

## 2. DAO 작성

- DAO(Data Access Object)는 DB 접근 로직 처리를 담당하는 별도의 객체를 의미한다.
- 기존의 DataBase 클래스를 대체하는 DAO 클래스를 작성한다.
- java.sql의 `Connection`, `PreparedStatement`, `ResultSet`을 사용해 DB를 접근할 수 있도록 하는 create/get/update 메서드를 작성한다.
- 테스트 코드를 작성해 정상 작동 여부를 확인한다.
- DataBase 클래스 대신 DAO를 사용하도록 Controller 코드를 변경하면 정상적으로 작동하는 것을 확인할 수 있다.

---
- 현재 UserDao는 Connection, PreparedStatement 초기화/제거, try/catch문이 중복되어 있다.
- 변화가 발생하는 부분과 아닌 부분을 정리해 보면 아래와 같다.

    | 작업 | 변화 발생 여부 |
    | --- | --- |
    | Connection 관리 | X |
    | SQL | O |
    | Statement 관리 | X |
    | ResultSet 관리 | X |
    | Row 데이터 추출 | O |
    | 파라미터 선언 | O |
    | 파라미터 setting | X |
    | 트랜잭션 관리 | X |
- 즉 SQL, Row 데이터 추출, 파라미터 선언만 직접 구현하고 나머지는 공통 라이브러리로 추출할 수 있다.
- ⌜expert one-on-one J2EE 설계와 개발⌟에 따르면 **컴파일 Exception을 사용해야 하는 경우는 API를 사용하는 모든 곳에서 해당 예외를 처리해야 하는 경우**뿐이다. SQLException은 컴파일 Exception이기 때문에 매번 try/catch문을 사용해야 하는데, 이를 런타임 Exception으로 변경해 소스코드 가독성을 높여 보자.

# DAO 리팩토링

## 1. UserDao 책임 분리

### 메서드 분리

> 변화 발생/발생 X 부분을 메서드로 추출해 분리한다.
>
- 추출한 UserDao의 메서드는 다음과 같다.
    - `insert(User)`: Connection 생성, sql 생성, sql에 User 정보 세팅, 데이터베이스에 저장
    - `setValuesForInsert(User, PreparedStatement)`: PreparedStatement에 User 데이터를 세팅
    - `createQueryForInsert()`: insert를 위한 sql 쿼리 생성
    - `update(User)`: User 정보 업데이트
    - `setValuesForUpdate(User, PreparedStatement)`: PreparedStatement에 User 데이터를 세팅
    - `createQueryForUpdate()`: Connection 생성, sql 생성, sql에 User 정보 세팅, 데이터베이스에 User 정보 업데이트
    - `findAll()`: Users 전체 정보 반환
    - `findByUserId(String)`: User 정보 반환

### 공통 라이브러리로 구현할 부분을 새로운 클래스로 이동

> 새로운 클래스를 생성해 공통 라이브러리로 구현할 부분을 이동한다.
>
- insert, update를 수행할 공통 클래스인 InsertJdbcTemplate, UpdateJdbcTemplate을 생성하고 공통으로 사용되는 메서드를 이동시킨다.
- `insert`, `update` 메서드를 각각 공통 클래스로 이동한다.
- UserDao의 `insert`는 InsertJdbcTemplate의 `insert`를 호출하도록 처리한다.

### 공통 라이브러리의 UserDao 의존관계 제거

> setValueForInsert(), createQueryForInsert()가 InsertJdbcTemplate에 없기 때문에 UserDao에 대한 의존성을 가진다.
>
- 의존성을 제거하지만 구현을 InsertJdbcTemplate가 담당하지 않도록 하기 위해 두 메서드를 InsertJdbcTemplate에서 추상 메서드로 구현하고, UserDao에서 구현을 담당하도록 한다.
- UserDao에서 익명 클래스를 사용해 구현한다.
- **추상 메서드(Abstract Method)**
    - 추상 메서드는 인터페이스나 추상 클래스에서 선언된 메서드이다. 구현 없이 선언만 된 메서드이고, 자식 클래스에서 반드시 구현해야 한다.
- **익명 클래스(Anonymous Class)**
    - 익명 클래스는 내부 클래스의 일종으로 이름이 없는 클래스를 의미한다. 이름이 없기 때문에 사용되는 현재 이외의 다른 상황에서 호출할 수 없기 때문에, 일회성 클래스로 사용되고 버려진다.
    - 클래스 정의와 동시에 객체를 생성할 수 있다. 어떤 메서드에서 부모 클래스의 자원을 상속받아 재정의하여 사용할 자식 클래스가 한번만 사용되고 버려질 자료형이라면 익명 클래스로 정의하는 것이 좋다.

### 공통 라이브러리 통합하기

> 세부 구현을 UserDao의 `insert`, `update` 메서드에게 위임했기 때문에 InsertJdbcTemplate, UpdateJdbcTemplate를 통합할 수 있다.
>
- JdbcTemplate을 생성해 두 공통 라이브러리를 통합할 수 있다.

### User에 대한 의존관계 제거

> JdbcTemplate의 insert, update를 User 이외의 도메인(모델)에서도 사용하기 위해 User 클래스에 대한 의존성을 제거한다.
>
- `setValues` 메서드에 user를 전달하는 방식이 아니라 UserDao에서 구현된 setValues 메서드에서 user 인스턴스에 직접 접근하도록 변경한다.

### createQuery 제거

> JdbcTemplate의 `createQuery` 추상 메서드 대신 UserDao의 `update` 메서드 매개변수로 sql을 전달해 매번 추상 메서드를 정의하지 않아도 되도록 한다.
>
- `createQuery`는 String 형의 sql을 반환하는 작업만 수행하기 때문에 메서드 대신 매개변수로 변환한다.

### SELECT를 위한 메서드 추가

> `findAll`, `findById` 메서드에서 사용할 query 메서드를 JdbcTemplate에 추가한다.
>
- `query`는 sql 쿼리를 실행하고 결과를 List<Object>로 받아오도록 구현한다.
- SELECT 결과를 자바 객체로 변환하는 `MapRow` 추상 메서드를 추가하고 UserDao의 `findAll`에서 구현하도록 한다.
- `findAll`의 경우 `query`를 그대로 사용하고, `findById`의 경우 List<Object>의 0번째 요소를 받아오도록 하는 `queryForObject`를 정의해 사용하도록 한다.

### setValues, mapRow간 의존관계 제거

> JdbcTemplate의 2개의 추상 메서드를 인터페이스로 분리한다.
>
- mapRow는 SELECT 쿼리가 아니면 필요하지 않은데, JdbcTemplate에 추상 메서드로 정의되어 있어 JdbcTemplate를 사용하는 UserDao의 `insert`, `update` 메서드에서도 구현해야 한다.
- setValues와 mapRow 메서드를 분리해 서로간 의존관계를 제거하면 위 문제를 해결할 수 있다. 각 메서드 별 인터페이스를 생성하고 메서드를 정의한다.
- JdbcTemplate의 각 메서드에 필요한 인터페이스를 인수로 전달하고 정의한다. UserDao에서 인수로 전달한 인터페이스의 메서드를 구현한다.

## 2. SQLException을 Runtime Exception으로 처리

- 현재 UserDao에서는 모든 메서드가 컴파일타임 Exception인 SQLException을 던진다. 런타임 Exception을 추가해 문제를 해결하자.

### 새 Exception 추가

> RuntimeException을 상속하는 DataAccessException을 추가한다.
>
- message, cause 등 여러 인수에 대한 생성자를 가지는 DataAccessException 클래스를 생성한다.
- JdbcTemplate에서 DataAccessException를 던지게 수정해서 더 이상 SQLException을 처리하지 않도록 한다.

### try-with-resource로 finally 제거

> PreparedStatememt, Connection의 사용 후 반납을 위해 try-with-resource를 사용한다.
>

## 3. 추가 개선

### 제네릭으로 캐스팅 제거

> 데이터 조회 시 캐스팅을 제거하기 위해 제네릭을 사용할 수 있다.
>
- mapRow의 반환값에 제네릭을 적용하고 JdbcTemplate에도 적용한다.

### 가변 인자 적용

> 가변 인자를 사용해 값을 전달하면 전달 값의 개수가 동적으로 변경되는 경우에도 적용할 수 있다.
>

### 람다식으로 익명 클래스 개선

> RowMapper 익명 클래스 생성 부분을 람다식으로 개선해 좀 더 깔끔하게 변경할 수 있다.
>
- 람다식을 사용하면 메서드 인자, 메서드 구현부만 구현해 전달이 가능하다.
- 람다를 사용하려면 인터페이스에 메서드가 하나만 존재해야 한다. 또 해당 인터페이스에 @FunctionalInterface를 추가해야 한다.

# 콜백 인터페이스

## Callback

> Callback이란 한 클래스가 다른 클래스에 실행을 요청하여 실행이 진행되는 도중 특정 이벤트가 발생하면 요청한 클래스에서 정의한 함수를 실행하도록 하는 개념이다.
>
- 변화 시점이 다른 부분을 서로 다른 인터페이스로 분리함으로써 공통 라이브러리에 대한 유연함을 높일 수 있다.