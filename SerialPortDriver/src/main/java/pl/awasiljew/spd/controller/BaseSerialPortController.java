package pl.awasiljew.spd.controller;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.apache.log4j.Logger;
import pl.awasiljew.spd.data.SerialRequest;
import pl.awasiljew.spd.data.SerialResponse;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.port.factory.SerialPortFactory;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.SerialPortSettings;
import pl.awasiljew.spd.utils.HexDecoder;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * @author Adam Wasiljew
 */
public abstract class BaseSerialPortController {

    private static final Logger log = Logger.getLogger(BaseSerialPortController.class);
    private SerialPortSettings serialPortSettings;
    private SerialPortInstanceFactory serialPortInstanceFactory;
    private SerialPort serialPort;
    private SerialPortEventListener listener;
    private volatile boolean isOpen;

    protected BaseSerialPortController(SerialPortSettings serialPortSettings, SerialPortInstanceFactory serialPortInstanceFactory) {
        this.serialPortSettings = serialPortSettings;
        this.serialPortInstanceFactory = serialPortInstanceFactory;
    }

    public synchronized boolean isOpen() {
        return isOpen;
    }

    synchronized public SerialResponse send(SerialRequest req) throws PortClosedException, IOException {
        if (isOpen) {
            byte[] respFrame = send(req.getFrame());
            if (respFrame == null) {
                return null;
            } else {
                return createResponseObject(respFrame);
            }
        } else {
            throw new PortClosedException();
        }
    }

    synchronized public void sendAsync(SerialRequest req) throws PortClosedException, IOException {
        if (isOpen) {
            sendAsync(req.getFrame());
        } else {
            throw new PortClosedException();
        }
    }

    private byte[] send(byte[] bytes) throws IOException {
        log.debug("Sending frame: [" + HexDecoder.getHexString(bytes) + "]");
        serialPort.getOutputStream().write(bytes);
        serialPort.getOutputStream().flush();
        return readFrame();
    }

    private void sendAsync(byte[] bytes) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending frame: [" + HexDecoder.getHexString(bytes) + "]");
        }
        serialPort.getOutputStream().write(bytes);
        serialPort.getOutputStream().flush();
    }

    abstract protected byte[] readFrame() throws IOException;

    /**
     * Used actually for closing streams if opened
     */
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
        listener = createPortSerialListener(serialPort);
        serialPort.addEventListener(listener);
        serialPort.notifyOnDataAvailable(true);
    }

    synchronized public void open() {
        if (!isOpen) {
            try {
                initSerialPort();
                attachSerialPortListener();
                initStreams();
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

    private void initStreams() throws IOException {
        serialPort.getOutputStream();
        serialPort.getInputStream();
    }

    private void initSerialPort() throws UnsupportedCommOperationException, PortInUseException {
        serialPort = new SerialPortFactory(serialPortInstanceFactory, serialPortSettings).createSerialPort();
    }

    protected abstract SerialPortEventListener createPortSerialListener(SerialPort serialPort);

    protected abstract SerialResponse createResponseObject(byte[] respFrame);

}
