package asg.dev;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class MainTest {
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue(true);
    }

    @Test
    public void isStringUtils() {
        assertTrue(org.apache.commons.lang3.StringUtils.capitalize("hello, world").equals("Hello, world"));
    }
}

