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
    public void add_null() {
        Assertions.assertEquals(0, calculator.add(" "));
    }

    @Test
    public void add_single_number() {
        Assertions.assertEquals(1, calculator.add("1"));
    }

    @Test
    public void add_number_with_default_delimiter() {
        Assertions.assertEquals(6, calculator.add("1,2,3"));
    }

    @Test
    public void add_number_with_custom_delimiter() {
        Assertions.assertEquals(6, calculator.add("//!\n1!2!3"));
    }

    @Test
    public void add_negative_number() {
        Assertions.assertThrows(RuntimeException.class, () -> calculator.add("3,-2,4"));
    }
}
