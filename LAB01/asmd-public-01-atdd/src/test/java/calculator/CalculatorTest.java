package calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    @Test
    void acceptanceTest(){
        Calculator calculator = new Calculator();
        calculator.enter(5);
        assertEquals(5, calculator.getResult());

        calculator.enter(6);
        calculator.add();
        assertEquals(11, calculator.getResult());

        calculator.enter(3);
        calculator.add();
        assertEquals(14, calculator.getResult());
        assertEquals(14, calculator.getResult());

        calculator.enter(3);
        assertThrows(IllegalStateException.class, () -> calculator.enter(5));
    }
}
