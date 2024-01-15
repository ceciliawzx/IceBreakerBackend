import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class SampleTest {

    SampleClass sampleClass = new SampleClass();

    @Test
    public void testAdd() {
        assertEquals(4, sampleClass.add(2, 2));
    }

    @Test
    public void testSubtract() {
        assertEquals(3, sampleClass.subtract(5, 2));
    }

    @Test
    public void testMultiply() {
        assertEquals(12, sampleClass.multiply(3, 4));
    }

    @Test
    public void testDivide() {
        assertEquals(2, sampleClass.divide(6, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDivideByZero() {
        sampleClass.divide(10, 0);
    }
}
