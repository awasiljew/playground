package pl.awasiljew.spd.controller.impl;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.awasiljew.spd.data.buffers.ByteFramesBuffer;
import pl.awasiljew.spd.data.buffers.ByteFramesBufferConfig;
import pl.awasiljew.spd.data.protocol.ByteDataParser;
import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.data.protocol.IdentifiedByteFrames;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;
import pl.awasiljew.spd.exception.UnexpectedResponseException;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.*;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * User: Adam
 * Date: 27.09.15
 * Time: 20:07
 */
public class SerialPortControllerImplTest {

    private static final byte START_BYTE = 'x';

    private EmulatedSerialPort serialPort;
    private SerialPortControllerImpl portController;

    @BeforeMethod
    public void setUp() throws Exception {
        ByteFramesBufferConfig config = new ByteFramesBufferConfig();
        config.setPollTimeout(200);
        ByteFramesBuffer framesBuffer = new ByteFramesBuffer(parser(), config);
        serialPort = new EmulatedSerialPort();
        portController = new SerialPortControllerImpl(
                buildSerialPortSettings(),
                new SerialPortInstanceFactory() {
                    @Override
                    public SerialPort createInstance(SerialPortSettings settings) throws PortInUseException {
                        return serialPort;
                    }
                },
                framesBuffer,
                framesBuffer
        );
        portController.open();
    }

    @Test
    public void shouldSendFrameAndReceiveResponse() throws SendFrameException, PortClosedException {
        // Given
        String request = "x4Adam";
        String expectedResponse = "x4Amad";
        simulateDataReadySync(expectedResponse);
        // When
        ByteFrame response = sendData(request);
        // Then
        assertDataEqualsFrame(expectedResponse, response);
    }

    @Test
    public void shouldSendFrameAndReceiveNullResponse() throws PortClosedException, SendFrameException {
        // Given
        String request = "x4Adam";
        // When
        ByteFrame response = sendData(request);
        // Then
        assertNull(response);
    }

    @Test(expectedExceptions = UnexpectedResponseException.class)
    public void shouldSendFrameAndDropUnexpectedResponse() throws PortClosedException, SendFrameException {
        // Given
        String request = "x4Adam";
        String expectedResponse = "x4Mati";
        simulateDataReadySync(expectedResponse);
        // When
        sendData(request);
        // Then
        // Expect exception
        fail();
    }

    private ByteFrame sendData(String request) throws PortClosedException, SendFrameException {
        return portController.send(new ByteFrame(request.getBytes()));
    }

    private void simulateDataReadySync(String msg) {
        Future<?> dataReady = serialPort.dataReady(msg.getBytes());
        try {
            dataReady.get(1000, TimeUnit.MILLISECONDS);
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    private void assertDataEqualsFrame(String data, ByteFrame lastFrame) {
        assertEquals(data, new String(lastFrame.getFrame()));
    }

    private ByteDataParser parser() {
        return new ByteDataParser() {
            @Override
            public IdentifiedByteFrames parse(byte[] data) {
                IdentifiedByteFrames frames = new IdentifiedByteFrames();
                int i = 0;
                while (i < data.length) {
                    if (data[i] == START_BYTE) { // We found start
                        if (i + 1 < data.length) {
                            try {  // Try to get rest of frame
                                int length = getFrameDataLength(data, i);
                                if (i + 1 + length < data.length) { // We have a full frame available
                                    int end = i + 1 + length;
                                    frames.addFrame(new ByteFrame(getFrame(data, i, length)));
                                    frames.setLength(end + 1);
                                    i = end + 1;
                                } else {
                                    i++;
                                }
                            } catch (NumberFormatException ex) {
                                i++;
                            }
                        } else {
                            i++;
                        }
                    } else {
                        i++;
                    }
                }
                return frames;
            }

            @Override
            public boolean expectedResponse(ByteFrame request, ByteFrame response) {
                return request.getFrame()[2] == response.getFrame()[2];
            }

            private byte[] getFrame(byte[] data, int i, int length) {
                byte[] frame = new byte[length + 2];
                System.arraycopy(data, i, frame, 0, frame.length);
                return frame;
            }

            private int getFrameDataLength(byte[] data, int i) {
                return Integer.parseInt("" + (char) data[i + 1]);
            }
        };
    }

    private SerialPortSettings buildSerialPortSettings() {
        return new SerialPortSettings(
                DataBits.BITS_8,
                FlowControl.NONE,
                Parity.NONE,
                StopBits.BITS_1,
                19200,
                "test",
                200);
    }
}
