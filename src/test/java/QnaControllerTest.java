import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import web_application_server.model.Answer;
import web_application_server.model.Question;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.dao.QuestionDao;

import java.util.Date;
import java.util.List;

public class QnaControllerTest {
    @Test
    public void getQuestionTest() {
        QuestionDao questionDao = new QuestionDao();
        Question actual = questionDao.findById(1L);
        Assertions.assertEquals(1L, actual.getQuestionId());
        Assertions.assertEquals("nahyun park", actual.getWriter());
        Assertions.assertEquals("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", actual.getTitle());
        Assertions.assertEquals(0, actual.getCountOfComment());
    }

    @Test
    public void findAllQuestionTest() {
        QuestionDao questionDao = new QuestionDao();
        Assertions.assertEquals(8, questionDao.findAll().size());
    }

    @Test
    public void getAnswerTest() {
        AnswerDao answerDao = new AnswerDao();
        List<Answer> actual = answerDao.findAllById(7L);
        Assertions.assertEquals("Hanghee Yi", actual.get(0).getWriter());
        Assertions.assertEquals("eungju", actual.get(1).getWriter());
    }

    @Test
    public void addAnswerTest() {
        AnswerDao answerDao = new AnswerDao();
        Answer answer = new Answer("answerTest", "contents", 1L);
        answerDao.insert(answer);

        Answer actual = answerDao.findById(6L);
        System.out.println("actual = " + actual);
    }
}
