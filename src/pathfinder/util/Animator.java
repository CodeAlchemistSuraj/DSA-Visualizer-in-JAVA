package pathfinder.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;

import javax.swing.SwingUtilities;

/**
 * Small utility to run animations steps on a background thread and marshal UI updates to EDT.
 */
public class Animator {
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void start(Runnable step, IntSupplier delaySupplier) {
        stop();
        running.set(true);
        worker = new Thread(() -> {
            while (running.get()) {
                try {
                    step.run();
                    int d = delaySupplier == null ? 1 : Math.max(1, delaySupplier.getAsInt());
                    Thread.sleep(d);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    public void stop() {
        running.set(false);
        if (worker != null) worker.interrupt();
        worker = null;
    }
}
