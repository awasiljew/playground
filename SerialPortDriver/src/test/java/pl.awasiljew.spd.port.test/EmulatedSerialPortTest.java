package pl.awasiljew.spd.port.test;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
    private String dataSent;
    private byte[] buffer;

    @BeforeMethod
    public void setUp() throws TooManyListenersException {
        serialPort = new EmulatedSerialPort();
        serialPort.setDataReceiver(new EmulatedSerialPort.DataReceiver() {
            @Override
            public void receive(byte[] data) {
                dataSent = data != null ? new String(data) : null;
            }
        });
        buffer = new byte[1024];
        dataReceived = null;
        dataSent = null;
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

    @Test
    public void shouldReceiveSentData() throws IOException {
        // Given
        String data = "12ASNDjj1";
        // When
        serialPort.getOutputStream().write(data.getBytes());
        // Then
        assertNotNull(dataSent);
        assertEquals(dataSent, data);
    }

    @Test
    public void shouldEmulateEchoServiceOnSerialPort() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Given
        final Future<?>[] dataReady = {null};
        serialPort.setDataReceiver(new EmulatedSerialPort.DataReceiver() {
            @Override
            public void receive(byte[] data) {
                dataSent = new String(data);
                dataReady[0] = serialPort.dataReady(dataSent.getBytes());
            }
        });
        String data = "12ASNDjj1";
        // When
        serialPort.getOutputStream().write(data.getBytes());
        dataReady[0].get(100, TimeUnit.MILLISECONDS);
        // Then
        assertNotNull(dataReceived);
        assertEquals(dataReceived, data);
    }

}
