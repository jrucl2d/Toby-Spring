package template_callback_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {
    Calculator calculator;
    String numFilePath;

    @BeforeEach
    void setup() {
        this.calculator = new Calculator();
        this.numFilePath = "/home/yuseonggeun/Documents/toby/1.1.1/src/test/java/template_callback_test/numbers.txt";
    }

    @Test
    void sumOfNumbers() throws IOException {
        int sum = calculator.calcSum(this.numFilePath);
        assertThat(sum).isEqualTo(15);
    }
    @Test
    void sumOfStrings() throws IOException{
        String res = calculator.concentrate(this.numFilePath);
        assertThat(res).isEqualTo("12345");
    }
    @Test
    void multipleOfNumbers() throws IOException {
        int res = calculator.calcMult(this.numFilePath);
        assertThat(res).isEqualTo(120);
    }
}
