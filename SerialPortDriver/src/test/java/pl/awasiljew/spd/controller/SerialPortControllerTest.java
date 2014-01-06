package pl.awasiljew.spd.controller;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.awasiljew.spd.controller.impl.BaseSerialPortController;
import pl.awasiljew.spd.data.ParsedSerialResponse;
import pl.awasiljew.spd.data.SerialRequest;
import pl.awasiljew.spd.data.SerialResponse;
import pl.awasiljew.spd.data.SerialResponseFactory;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.listener.DataWriteListener;
import pl.awasiljew.spd.port.settings.*;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;

import static org.testng.Assert.*;

/**
 * @author Adam Wasiljew
 */
public class SerialPortControllerTest {

    private SerialPortController serialPortController;
    private SerialPortSettings serialPortSettings;
    private SerialResponseFactory serialResponseFactory;
    private String expectedResponse;
    private String receivedResponse;
    private String writtenData;
    private EmulatedSerialPort serialPort;

    @BeforeMethod
    public void setUp() {
        expectedResponse = "";
        writtenData = null;
        serialPort = new EmulatedSerialPort();
        buildSerialPortSettings();
        buildSerialResponseFactory();
        setupDataWrittenListener();
        serialPortController = new BaseSerialPortController(
                serialPortSettings,
                new SerialPortInstanceFactory() {
                    @Override
                    public SerialPort createInstance(SerialPortSettings settings) throws PortInUseException {
                        return serialPort;
                    }
                },
                serialResponseFactory
        );
    }

    @AfterMethod
    public void tearDown() {
        serialPortController.close();
    }

    @Test
    public void shouldSendAndReceiveResponse() throws PortClosedException, SendFrameException {
        // Given
        TestSerialRequest request = new TestSerialRequest("Req1");
        expectedResponse = "Res";
        // When
        simulateDataResponse(expectedResponse.getBytes(), 100);
        serialPortController.open();
        SerialResponse response = serialPortController.send(request);
        // Then
        assertNotNull(response);
        assertTrue(response instanceof TestSerialResponse);
        assertEquals(expectedResponse, ((TestSerialResponse) response).getText());
    }

    @Test(expectedExceptions = PortClosedException.class)
    public void shouldThrowExceptionWhenPortIsClosed() throws PortClosedException, SendFrameException {
        // Given
        TestSerialRequest request = new TestSerialRequest("Req1");
        // When
        serialPortController.send(request);
        // Then - expected exception
    }

    @Test
    public void shouldReturnNullResponseAfterTimeout() throws PortClosedException, SendFrameException {
        // Given
        TestSerialRequest request = new TestSerialRequest("Req1");
        // When
        serialPortController.open();
        SerialResponse response = serialPortController.send(request);
        // Then
        assertNull(response);
    }

    private void buildSerialResponseFactory() {
        serialResponseFactory = new SerialResponseFactory() {
            @Override
            public ParsedSerialResponse buildResponse(byte[] data) {
                if (data.length > 2) {
                    String candidate = new String(data, 0, 3);
                    if (expectedResponse.equals(candidate)) {
                        return new ParsedSerialResponse(
                                true,
                                3,
                                new TestSerialResponse(candidate)
                        );
                    }
                }
                return new ParsedSerialResponse(false, -1, null);
            }
        };
    }

    private void buildSerialPortSettings() {
        serialPortSettings = new SerialPortSettings(
                DataBits.BITS_8,
                FlowControl.NONE,
                Parity.NONE,
                StopBits.BITS_1,
                19200,
                "test",
                200);
    }

    public class TestSerialRequest implements SerialRequest {

        private String text;

        public TestSerialRequest(String text) {
            this.text = text;
        }

        @Override
        public byte[] getFrame() {
            return text.getBytes();
        }

        public String getText() {
            return text;
        }
    }

    public class TestSerialResponse implements SerialResponse {

        private String text;

        public TestSerialResponse(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private void setupDataWrittenListener() {
        serialPort.addDataWrittenOutListener(new DataWriteListener() {
            @Override
            public void dataWritten() {
                writtenData = new String(serialPort.consumeWrittenData());
            }
        });
    }

    private void simulateDataResponse(final byte[] data, final long delay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    // Ignore
                    e.printStackTrace();
                }
                serialPort.simulateDataReady(data);
            }
        }).start();
    }
}
