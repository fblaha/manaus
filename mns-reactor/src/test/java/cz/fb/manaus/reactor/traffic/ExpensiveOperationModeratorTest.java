package cz.fb.manaus.reactor.traffic;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Range.closed;
import static org.junit.Assert.assertTrue;

public class ExpensiveOperationModeratorTest {
    private ExpensiveOperationModerator operationModerator = new ExpensiveOperationModerator(Duration.ofMillis(333), "test moderator");

    @Test
    public void testModerateSecondRate() throws Exception {
        Stopwatch stopwatch = Stopwatch.createUnstarted().start();
        for (int i = 0; i < 15; i++) {
            suspendAndPrint(i);
        }
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(" ... duration " + Long.toString(elapsed));
        assertTrue(closed(4 * DateUtils.MILLIS_PER_SECOND, 5 * DateUtils.MILLIS_PER_SECOND).contains(elapsed));
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionFail() throws Exception {
        ExpensiveOperationModerator moderator = new ExpensiveOperationModerator(Duration.ofSeconds(1), "test");
        moderator.exceptionOnExceeded();
        Thread.sleep(10);
        moderator.exceptionOnExceeded();
    }

    @Test
    public void testExceptionPass() throws Exception {
        ExpensiveOperationModerator moderator = new ExpensiveOperationModerator(Duration.ofMillis(10), "test");
        moderator.exceptionOnExceeded();
        Thread.sleep(10);
        moderator.exceptionOnExceeded();
    }

    private void suspendAndPrint(int i) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        operationModerator.suspendOnExceeded();
        System.out.println(MessageFormat.format("elapsed = {0}    ({1})", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS), i));
    }

}
