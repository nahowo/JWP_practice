import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringCalculatorTest {
    private StringCalculator calculator;
    @BeforeEach
    public void setUp() {
        calculator = new StringCalculator();
    }

    @Test
    public void checkAddTest() {
        String[] ret1 = calculator.checkDelimiter("//!\n2!28!3");
        Assertions.assertEquals(33, calculator.add(ret1));

        String[] ret2 = calculator.checkDelimiter("3,4,5");
        Assertions.assertEquals(12, calculator.add(ret2));

        String[] ret3 = calculator.checkDelimiter(" ");
        Assertions.assertEquals(0, calculator.add(ret3));

        Assertions.assertThrows(RuntimeException.class, () -> calculator.checkDelimiter("3-2:1"));
        Assertions.assertThrows(RuntimeException.class, () -> calculator.checkDelimiter("//!\n2-3!23"));
    }
}
