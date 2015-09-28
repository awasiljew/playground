package pl.awasiljew.spd.controller.impl;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.awasiljew.spd.data.buffers.ByteFramesBuffer;
import pl.awasiljew.spd.data.buffers.ByteFramesBufferConfig;
import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;
import pl.awasiljew.spd.exception.UnexpectedResponseException;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.*;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;
import pl.awasiljew.spd.port.test.protocol.InverseEchoProtocol;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * User: Adam
 * Date: 27.09.15
 * Time: 20:07
 */
public class SerialPortControllerImplEchoProtocolTest {

    private EmulatedSerialPort serialPort;
    private SerialPortControllerImpl portController;

    @BeforeMethod
    public void setUp() throws Exception {
        ByteFramesBufferConfig config = new ByteFramesBufferConfig();
        config.setPollTimeout(200);
        serialPort = new EmulatedSerialPort();
        InverseEchoProtocol echoProtocol = new InverseEchoProtocol(serialPort);
        ByteFramesBuffer framesBuffer = new ByteFramesBuffer(echoProtocol, config);
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
    public void shouldSendFrameAndReceiveInverseResponse() throws PortClosedException, SendFrameException {
        // Given
        String request = "x4Adam";
        // When
        ByteFrame response = sendData(request);
        // Then
        assertDataEqualsFrame("x4madA", response);
    }

    @Test(expectedExceptions = UnexpectedResponseException.class)
    public void shouldSendFrameAndDropUnexpectedResponse() throws PortClosedException, SendFrameException {
        // When
        portController.send(new ByteFrame(InverseEchoProtocol.UNSUPPORTED_MESSAGE));
        // Then
        // Expect exception
        fail();
    }

    private ByteFrame sendData(String request) throws PortClosedException, SendFrameException {
        return portController.send(new ByteFrame(request.getBytes()));
    }

    private void assertDataEqualsFrame(String data, ByteFrame lastFrame) {
        assertEquals(data, new String(lastFrame.getFrame()));
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
