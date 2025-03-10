import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalculatorTest {
    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void addTest() {
        Assertions.assertEquals(9, calculator.add(3, 6));
    }

    @Test
    public void subtractTest() {
        Assertions.assertEquals(2, calculator.subtract(10, 8));
    }

    @Test
    public void multiplyTest() {
        Assertions.assertEquals(12, calculator.multiply(3, 4));
    }

    @Test
    public void divideTest() {
        Assertions.assertEquals(6, calculator.divide(24, 4));
    }
}
