package pointcutTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.assertj.core.api.Assertions.assertThat;

public class pointcutTest {

    @Test
    void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(int minus(int,int))");

        assertThat(pointcut.getClassFilter().matches(Target.class)
                && pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null)).isTrue();

        assertThat(pointcut.getClassFilter().matches(Target.class)
        && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null)).isFalse();

        assertThat(pointcut.getClassFilter().matches(Bean.class)
        && pointcut.getMethodMatcher().matches(
                Target.class.getMethod("method"), null
        )).isFalse();
    }
}
