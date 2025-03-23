DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS
(
    userId   varchar(12) NOT NULL,
    password varchar(12) NOT NULL,
    name     varchar(20) NOT NULL,
    email    varchar(50) NOT NULL,

    PRIMARY KEY (userId)
);

INSERT INTO USERS
VALUES ('admin', 'password', 'admin', 'admin@naver.com');
INSERT INTO USERS
VALUES ('nahowo', '1234', 'nahyun park', 'nahowo@naver.com');