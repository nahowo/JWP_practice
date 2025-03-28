import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import web_application_server.model.Answer;
import web_application_server.model.Question;
import web_server_launcher.core.ConnectionManager;
import web_server_launcher.dao.AnswerDao;
import web_server_launcher.dao.QuestionDao;

import java.util.List;

public class QnaDaoTest {
    private QuestionDao questionDao;
    @BeforeEach
    public void setUp() {
        questionDao = new QuestionDao();
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }
    @Test
    public void getQuestionTest() {
        Question actual = questionDao.findById(1L);
        Assertions.assertEquals(1L, actual.getQuestionId());
        Assertions.assertEquals("nahyun park", actual.getWriter());
        Assertions.assertEquals("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?", actual.getTitle());
        Assertions.assertEquals(0, actual.getCountOfComment());
    }

    @Test
    public void findAllQuestionTest() {
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

    @Test
    public void deleteAnswerTest() {
        AnswerDao answerDao = new AnswerDao();
        answerDao.delete(5);
        Assertions.assertEquals(2, answerDao.findAllById(8).size());
        Assertions.assertEquals(2, questionDao.findById(8L).getCountOfComment());
    }
}
