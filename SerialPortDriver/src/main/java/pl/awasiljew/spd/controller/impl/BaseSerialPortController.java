package pl.awasiljew.spd.controller.impl;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.apache.log4j.Logger;
import pl.awasiljew.spd.controller.SerialPortController;
import pl.awasiljew.spd.controller.io.SerialPortIOStream;
import pl.awasiljew.spd.controller.listener.ListenerSerialHandler;
import pl.awasiljew.spd.data.SerialRequest;
import pl.awasiljew.spd.data.SerialResponse;
import pl.awasiljew.spd.data.SerialResponseFactory;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;
import pl.awasiljew.spd.port.factory.SerialPortFactory;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * @author Adam Wasiljew
 */
public class BaseSerialPortController implements SerialPortController {

    private static final Logger log = Logger.getLogger(BaseSerialPortController.class);
    private SerialPortSettings serialPortSettings;
    private SerialPortInstanceFactory serialPortInstanceFactory;
    private SerialPort serialPort;
    private volatile boolean isOpen;
    private SerialResponseFactory serialResponseFactory;
    private SerialPortIOStream serialPortIOStream;

    public BaseSerialPortController(SerialPortSettings serialPortSettings, SerialPortInstanceFactory serialPortInstanceFactory, SerialResponseFactory serialResponseFactory) {
        this.serialPortSettings = serialPortSettings;
        this.serialPortInstanceFactory = serialPortInstanceFactory;
        this.serialResponseFactory = serialResponseFactory;
    }

    @Override
    public synchronized boolean isOpen() {
        return isOpen;
    }

    @Override
    synchronized public SerialResponse send(SerialRequest req) throws PortClosedException, SendFrameException {
        if (isOpen) {
            try {
                return serialPortIOStream.send(req);
            } catch (IOException e) {
                serialPortIOStream.reset();
                throw new SendFrameException(e);
            } catch (InterruptedException e) {
                serialPortIOStream.reset();
                throw new SendFrameException(e);
            }
        } else {
            throw new PortClosedException();
        }
    }

    @Override
    synchronized public void sendAsync(SerialRequest req) throws PortClosedException, SendFrameException {
        if (isOpen) {
            try {
                serialPortIOStream.sendAsync(req);
            } catch (IOException e) {
                serialPortIOStream.reset();
                throw new SendFrameException(e);
            }
        } else {
            throw new PortClosedException();
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
                    log.error(ex, ex);
                }
                isOpen = false;
            }
        }
    }

    private void attachSerialPortListener() throws TooManyListenersException {
        serialPort.addEventListener(createPortSerialListener());
        serialPort.notifyOnDataAvailable(true);
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

    private void initSerialPort() throws UnsupportedCommOperationException, PortInUseException {
        serialPort = new SerialPortFactory(serialPortInstanceFactory, serialPortSettings).createSerialPort();
    }

    private SerialPortEventListener createPortSerialListener() {
        return new ListenerSerialHandler(serialPort, serialPortIOStream);
    }

    private void initPortIOStream() throws IOException {
        serialPort.getOutputStream();
        serialPort.getInputStream();
        serialPortIOStream = new SerialPortIOStream(serialPort, serialResponseFactory, serialPortSettings);
    }

}
