package pl.awasiljew.spd.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.*;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Adam Wasiljew
 */
public class MultithreadTestSupport {

    private static Logger log = LoggerFactory.getLogger(MultithreadTestSupport.class);
    private List<SimpleConcurrentTask> concurrentTestTasks;
    private StringBuffer logData;
    private Integer count = 0;

    @BeforeMethod
    public void setUp() {
        concurrentTestTasks = new ArrayList<SimpleConcurrentTask>();
        logData = new StringBuffer();
    }

    @Test
    public void shouldDoNothing() {
        submitTask(new SimpleConcurrentTask(1));
        submitTask(new SimpleConcurrentTask(2));
        submitTask(new SimpleConcurrentTask(3));

        Collection<List<SimpleConcurrentTask>> permutationOfConcurrentTasks = Collections2.orderedPermutations(concurrentTestTasks);
        long i = 1;
        List<List<Integer>> listOfResults = new ArrayList<List<Integer>>(permutationOfConcurrentTasks.size());
        for (List<SimpleConcurrentTask> taskList : permutationOfConcurrentTasks) {
            List<Long> ids = Lists.transform(taskList, new Function<ConcurrentTestTask, Long>() {
                @Override
                public Long apply(ConcurrentTestTask task) {
                    return task.taskId();
                }
            });
            count = 0;
            log("====== Phase: " + (i++) + " " + ids);
            ExecutorService executorService = Executors.newFixedThreadPool(concurrentTestTasks.size());
            List<Future<Integer>> submittedTasks = new ArrayList<Future<Integer>>(taskList.size());
            for (ConcurrentTestTask concurrentTestTask : taskList) {
                submittedTasks.add(executeInSeparateThread(concurrentTestTask, executorService));
            }
            List<Integer> results = barrier(submittedTasks);
            listOfResults.add(results);
            log("====== Phase END ======");
        }
        log.info("\n" + logData.toString());
        for (List<Integer> integers : listOfResults) {
            assertPermutations(integers, asList(1, 2, 3));
        }
    }

    private void assertPermutations(List<Integer> result, List<Integer> expected) {
        assertEquals(result.size(), expected.size());
        for (Integer i : expected) {
            if (!result.contains(i)) {
                assertFalse(true, "Not found expected " + expected + " in " + result);
            }
        }
    }

    private <E> List<E> barrier(List<Future<E>> futureList) {
        List<E> resultList = new ArrayList<E>(futureList.size());
        for (Future<E> f : futureList) {
            try {
                resultList.add(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return resultList;
    }

    private <E, T> Future<E> executeInSeparateThread(final ConcurrentTestTask<E, T> task, ExecutorService executorService) {
        return executorService.submit(new Callable<E>() {
            @Override
            public E call() throws Exception {
                long s = new Random(new Date().getTime()).nextInt(100);
                try {
                    Thread.sleep(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return task.executeTask();
            }
        });
    }

    public void submitTask(SimpleConcurrentTask task) {
        concurrentTestTasks.add(task);
    }

    public synchronized void log(String log) {
        logData.append(log).append("\n");
    }

    public interface ConcurrentTestTask<E, T> extends Comparable<T> {

        E executeTask();

        long taskId();

    }

    public class SimpleConcurrentTask implements ConcurrentTestTask<Integer, ConcurrentTestTask> {

        private long id;
        private int counter = 0;

        public SimpleConcurrentTask(long id) {
            this.id = id;
        }

        @Override
        public Integer executeTask() {
            synchronized (SimpleConcurrentTask.class) {
                counter = getCount();
                counter = counter + 1;
                setCount(counter);
                log("Task : " + id + ", count = " + getCount());
            }
            return counter;
        }

        @Override
        public long taskId() {
            return id;
        }

        @Override
        public int compareTo(ConcurrentTestTask o) {
            return Long.valueOf(this.id).compareTo(Long.valueOf(o.taskId()));
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
