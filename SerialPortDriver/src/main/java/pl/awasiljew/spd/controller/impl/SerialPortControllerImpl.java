package pl.awasiljew.spd.controller.impl;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.awasiljew.spd.controller.SerialPortController;
import pl.awasiljew.spd.controller.listener.ListenerSerialHandler;
import pl.awasiljew.spd.data.SerialDataReceiver;
import pl.awasiljew.spd.data.buffers.SerialDataFramesBuffer;
import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;
import pl.awasiljew.spd.exception.UnexpectedResponseException;
import pl.awasiljew.spd.port.factory.SerialPortFactory;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 * @author Adam Wasiljew
 */
public class SerialPortControllerImpl implements SerialPortController {

    private static final Logger log = LoggerFactory.getLogger(SerialPortControllerImpl.class);
    private SerialPortSettings serialPortSettings;
    private SerialPortInstanceFactory serialPortInstanceFactory;
    private SerialPort serialPort;
    private volatile boolean isOpen;
    private SerialDataFramesBuffer framesBuffer;
    private SerialDataReceiver serialDataReceiver;

    public SerialPortControllerImpl(SerialPortSettings serialPortSettings, SerialPortInstanceFactory serialPortInstanceFactory, SerialDataFramesBuffer framesBuffer, SerialDataReceiver serialDataReceiver) {
        this.serialPortSettings = serialPortSettings;
        this.serialPortInstanceFactory = serialPortInstanceFactory;
        this.framesBuffer = framesBuffer;
        this.serialDataReceiver = serialDataReceiver;
    }

    @Override
    public synchronized boolean isOpen() {
        return isOpen;
    }

    @Override
    synchronized public ByteFrame send(ByteFrame req) throws PortClosedException, SendFrameException {
        if (isOpen) {
            try {
                write(req);
                return read(req);
            } catch (IOException e) {
                throw new SendFrameException(e, req);
            }
        } else {
            throw new PortClosedException();
        }
    }

    @Override
    synchronized public void sendAsync(ByteFrame req) throws PortClosedException, SendFrameException {
        if (isOpen) {
            try {
                write(req);
            } catch (IOException e) {
                throw new SendFrameException(e, req);
            }
        } else {
            throw new PortClosedException();
        }
    }

    @Override
    public ByteFrame readLast() throws PortClosedException {
        if (isOpen) {
            return framesBuffer.getLastFrame();
        } else {
            throw new PortClosedException();
        }
    }

    @Override
    synchronized public void open() {
        if (!isOpen) {
            try {
                initSerialPort();
                initPortIOStream();
                attachSerialPortListener();
                isOpen = true;
                return;
            } catch (TooManyListenersException ex) {
                log.error("Error while attaching listener to serial port! " + ex, ex);
            } catch (IOException ex) {
                log.error("Error while getting streams to serial port! " + ex, ex);
            } catch (UnsupportedCommOperationException ex) {
                log.error("Error while creating serial port! " + ex, ex);
            } catch (PortInUseException ex) {
                log.error("Error during set up serial port:" + ex, ex);
            }
            isOpen = false;
        }
    }

    @Override
    synchronized public void close() {
        if (isOpen) {
            try {
                if (serialPort != null) {
                    if (serialPort.getInputStream() != null) {
                        serialPort.getInputStream().close();
                    }
                    if (serialPort.getOutputStream() != null) {
                        serialPort.getOutputStream().close();
                    }
                }
            } catch (IOException ex) {
                log.error("Error closing streams, not clean finish up: " + ex, ex);
            } finally {
                try {
                    if (serialPort != null) {
                        serialPort.close();
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
                isOpen = false;
            }
        }
    }

    private ByteFrame read(ByteFrame req) throws UnexpectedResponseException {
        ByteFrame response = framesBuffer.getLastFrame();
        if (response != null) {
            if (!framesBuffer.expectedFrame(req, response)) {
                framesBuffer.reset();
                throw new UnexpectedResponseException("Response frame valid, but unexpected", response);
            }
        }
        return response;
    }

    private void write(ByteFrame req) throws IOException {
        OutputStream outputStream = serialPort.getOutputStream();
        if (outputStream != null) {
            outputStream.write(req.getFrame());
        } else {
            throw new IOException("Output stream of serial port is null!");
        }
    }

    private void attachSerialPortListener() throws TooManyListenersException {
        serialPort.addEventListener(createPortSerialListener());
        serialPort.notifyOnDataAvailable(true);
    }

    private void initSerialPort() throws UnsupportedCommOperationException, PortInUseException {
        serialPort = new SerialPortFactory(serialPortInstanceFactory, serialPortSettings).createSerialPort();
    }

    private SerialPortEventListener createPortSerialListener() {
        return new ListenerSerialHandler(serialPort, serialDataReceiver);
    }

    private void initPortIOStream() throws IOException {
        serialPort.getOutputStream();
        serialPort.getInputStream();
    }

}
