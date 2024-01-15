import org.junit.Assert;
import org.junit.Test;

public class IceBreakerTest {

    @Test
    public void testAdd() {
        int a = 5;
        int b = 3;
        int expectedResult = 8;

        Assert.assertEquals(expectedResult, a+b);
    }
}