package pl.awasiljew.processors;

import pl.awasiljew.tasks.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Adam Wasiljew
 */
public class BlockingQueueProcessor {

    private BlockingQueue<Task> queue;
    private volatile boolean stop = false;
    private volatile boolean running = false;

    public BlockingQueueProcessor() {
        queue = new LinkedBlockingDeque<Task>();
    }

    public void start(Task task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void start() {
        if (!running) {
            running = true;
            stop = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!stop || (stop && queue.isEmpty())) {
                        try {
                            queue.take().run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    running = false;
                }
            }).start();
        }
    }

    public synchronized void stop() {
        stop = true;
    }
}
