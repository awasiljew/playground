package pl.awasiljew.spd.controller.listener;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.awasiljew.spd.data.SerialDataReceiver;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * User: Adam
 * Date: 25.09.15
 * Time: 10:46
 */
public class ListenerSerialHandlerTest {

    public static final String MESSAGE_SEPARATOR = "#";
    private ExecutorService executorService;
    private ListenerSerialHandler listener;
    private SerialDataReceiver consumer;
    private EmulatedSerialPort emulatedSerialPort;
    private List<String> receivedMessages;
    private List<Future<Boolean>> dataSendTasks;

    @BeforeMethod
    public void setUp() throws Exception {
        executorService = executorService = newFixedThreadPool(4);
        dataSendTasks = newArrayList();
        receivedMessages = newArrayList();
        emulatedSerialPort = new EmulatedSerialPort();
        consumer = new SerialDataReceiver() {
            @Override
            public void receive(byte[] data) {
                receivedMessages.add(new String(data));
            }
        };

        listener = new ListenerSerialHandler(emulatedSerialPort, consumer);
        emulatedSerialPort.addEventListener(listener);
    }

    @Test
    public void shouldReceiveData() {
        // Given
        String msg = "TEST";
        // When
        simulateDataReadySync(msg);
        // Then
        assertReceivedMessagesContains(msg);
    }

    @Test
    public void shouldNotReceiveIfEmpty() {
        // Given
        String msg = "";
        // When
        simulateDataReadySync(msg);
        // Then
        assertTrue(receivedMessages.isEmpty());
    }

    @Test
    public void shouldReceiveLargeDataExceedingInternalBufferSize() {
        // Given
        String longMessage = constructLongMessage();
        // When
        simulateDataReadySync(longMessage);
        // Then
        assertReceivedMessagesContains(longMessage);
    }

    @Test
    public void shouldReceiveMultipleMessages() {
        // Given
        String[] msgs = constructMessages(3);
        // When
        for (String msg : msgs) {
            simulateDataReadySync(msg);
        }
        // Then
        assertReceivedMessagesContains(msgs);
    }

    @Test
    public void shouldReceiveMultipleMessagesInRandomOrder() {
        // Given
        String[] msgs = constructMessages(100);
        // When
        for (String msg : msgs) {
            simulateDataReadyAtRandomTime(msg);
        }
        // Then
        assertReceivedMessagesContainsDataInRandomOrder(msgs);
    }

    private String[] constructMessages(int size) {
        String[] msgs = new String[size];
        for (int i = 0; i < msgs.length; i++) {
            if (i % 7 == 0) {
                msgs[i] = constructLongMessage() + MESSAGE_SEPARATOR;
            } else {
                msgs[i] = "TEST" + i + MESSAGE_SEPARATOR;
            }
        }
        return msgs;
    }

    private boolean dataSendTasksStatus() {
        for (Future<Boolean> task : dataSendTasks) {
            try {
                if (!task.get(2000, TimeUnit.MILLISECONDS)) {
                    return false;
                }
            } catch (Exception e) {
                // One of the task throws timeout
                return false;
            }
        }
        return true;
    }

    private void simulateDataReadyAtRandomTime(final String msg) {
        dataSendTasks.add(executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    long s = new Random(new Date().getTime()).nextInt(100);
                    try {
                        sleep(s);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                    emulatedSerialPort
                            .dataReady(msg.getBytes())
                            .get(1000, TimeUnit.MILLISECONDS);
                    return Boolean.TRUE;
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
            }
        }));
    }

    private void simulateDataReadySync(String msg) {
        Future<?> dataReady = emulatedSerialPort.dataReady(msg.getBytes());
        try {
            dataReady.get(1000, TimeUnit.MILLISECONDS);
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    private String constructLongMessage() {
        String longMessage = "LongMessage";
        while (longMessage.length() < ListenerSerialHandler.BUF_LEN * 2 + 10) {
            longMessage += longMessage;
        }
        return longMessage;
    }

    private void assertReceivedMessagesContains(String... messages) {
        assertTrue(receivedMessages.containsAll(newArrayList(messages)));
    }

    private void assertReceivedMessagesContainsDataInRandomOrder(String... messages) {
        assertTrue(dataSendTasksStatus(), "Not all send data task finished!");
        List<String> messageParts = restoreMessageParts();
        assertTrue(messageParts.containsAll(newArrayList(messages)));
    }

    private ImmutableList<String> restoreMessageParts() {
        return from(receivedMessages)
                .transformAndConcat(toMessageParts())
                .toList();
    }

    private Function<String, Iterable<String>> toMessageParts() {
        return new Function<String, Iterable<String>>() {
            @Override
            public Iterable<String> apply(String data) {
                return from(newArrayList(data.split(MESSAGE_SEPARATOR)))
                        .transform(toFullMessagePart())
                        .toList();
            }
        };
    }

    private Function<String, String> toFullMessagePart() {
        return new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + MESSAGE_SEPARATOR;
            }
        };
    }

}
