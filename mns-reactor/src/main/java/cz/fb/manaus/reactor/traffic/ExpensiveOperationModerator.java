package cz.fb.manaus.reactor.traffic;

import com.google.common.base.Throwables;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class ExpensiveOperationModerator {
    private static final Logger log = Logger.getLogger(ExpensiveOperationModerator.class.getSimpleName());

    private final Duration pause;
    private final String name;
    private final AtomicReference<Instant> lastTime = new AtomicReference<>();

    public ExpensiveOperationModerator(Duration pause, String name) {
        this.pause = pause;
        this.name = checkNotNull(name);
    }

    public void suspendOnExceeded() {
        handleFrequency(this::suspend);
    }

    public void exceptionOnExceeded() {
        handleFrequency(this::raise);
    }

    private void handleFrequency(Consumer<Duration> remainingHandler) {
        Instant last = lastTime.get();
        if (last == null) {
            lastTime.set(Instant.now());
        } else {
            Duration current = Duration.between(last, Instant.now());
            Duration remaining = pause.minus(current);
            if (remaining.isNegative() || remaining.isZero()) {
                lastTime.set(Instant.now());
            } else {
                remainingHandler.accept(remaining);
                handleFrequency(remainingHandler);
            }
        }
    }

    private void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    private void raise(Duration remaining) {
        throw new IllegalStateException(format("Too frequent call, remaining period ''{0}''", remaining));
    }

    private void suspend(Duration remaining) {
        log.log(Level.INFO, "Moderator ''{0}'' suspends execution for ''{1}''",
                new Object[]{name, remaining});
        sleep(remaining.toMillis());
    }

}
