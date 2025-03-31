package web_server_launcher.service;

import web_application_server.model.Answer;
import web_application_server.model.Question;

import java.util.List;

public class QnaService {
    public boolean hasZeroCountOfAnswers(Question question) {
        return (question.getCountOfComment() == 0);
    }

    public boolean isAllWrittenByLoginedUser(String name, List<Answer> answers) {
        for (Answer answer : answers) {
            if (!name.equals(answer.getWriter())) {
                return false;
            }
        }
        return true;
    }
}
