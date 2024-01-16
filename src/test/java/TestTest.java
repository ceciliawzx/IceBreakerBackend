import com.icebreaker.httprequests.HttpRequests;
import org.junit.Assert;
import org.junit.Test;

public class TestTest {
    @Test
    public void testAdd() {
        int a = 5;
        int b = 3;
        int expectedResult = 8;

        Assert.assertEquals(expectedResult, a+b);
    }

    @Test
    public void testRoomCreationConcurrent() throws InterruptedException {
        final int TOTAL_NUM_OF_THREADS = 20;
        final int TOTAL_NUM_OF_CALLS = 10000000;
        final int TOTAL_NUM_OF_CALLS_PER_THREAD = TOTAL_NUM_OF_CALLS / TOTAL_NUM_OF_THREADS;

        HttpRequests httpRequests = new HttpRequests();

        Thread[] threads = new Thread[TOTAL_NUM_OF_THREADS];

        for (int i = 0; i < TOTAL_NUM_OF_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < TOTAL_NUM_OF_CALLS_PER_THREAD; j++) {
                    httpRequests.handleRoomCreation("");
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < TOTAL_NUM_OF_THREADS; i++) {
            threads[i].join();
        }

        Assert.assertEquals(httpRequests.handleRoomCreation(""),
                "Room Created!!! Your New Room Number is " + TOTAL_NUM_OF_CALLS);
    }
}
