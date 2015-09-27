package pl.awasiljew.spd.port.test;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Adam Wasiljew
 */
public class EmulatedSerialPortTest {

    private EmulatedSerialPort serialPort;
    private String dataReceived;
    private byte[] buffer;

    @BeforeMethod
    public void setUp() throws TooManyListenersException {
        serialPort = new EmulatedSerialPort();
        buffer = new byte[1024];
        dataReceived = null;
        setupDataReceivedListener();
    }

    private void setupDataReceivedListener() throws TooManyListenersException {
        serialPort.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {
                synchronized (this) {
                    try {
                        switch (event.getEventType()) {
                            case SerialPortEvent.BI:
                            case SerialPortEvent.OE:
                            case SerialPortEvent.FE:
                            case SerialPortEvent.PE:
                            case SerialPortEvent.CD:
                            case SerialPortEvent.CTS:
                            case SerialPortEvent.DSR:
                            case SerialPortEvent.RI:
                            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                                // Do nothing
                                break;
                            case SerialPortEvent.DATA_AVAILABLE:
                                int bytes = serialPort.getInputStream().read(buffer);
                                dataReceived = new String(buffer, 0, bytes);
                                break;
                        }
                    } catch (IOException ex) {
                        // Ignore
                    }
                }
            }
        });
    }

    @Test
    public void shouldNotifyListenerThatDataAvailable() throws IOException, InterruptedException, TimeoutException, ExecutionException {
        // Given
        String data = "12ASNDjj1";
        // When
        serialPort.dataReady(data.getBytes()).get(100, TimeUnit.MILLISECONDS);
        // Then
        assertNotNull(dataReceived);
        assertEquals(dataReceived, data);
    }

}
