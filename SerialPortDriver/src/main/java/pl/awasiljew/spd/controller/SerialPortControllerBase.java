package pl.awasiljew.spd.controller;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.apache.log4j.Logger;
import pl.awasiljew.spd.data.SerialRequest;
import pl.awasiljew.spd.data.SerialResponse;
import pl.awasiljew.spd.port.SerialPortFactory;
import pl.awasiljew.spd.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 * <p>Class wrapper for serial port. It allows to communictae through serial port by sending
 * specially prepared frames. Frames are prepared by helper classes and classes which implements
 * {@link SerialRequest}.</p>
 * <p/>
 * <p>Serial port cotroller can operate in two modes. First mode (which is set at instance creation)
 * allows only to send frame to serial port and wait until serial port return frame with response.
 * The second mode allows to start default listener method, to listen for incoming data and
 * handle it.</p>
 *
 * @author adam
 */
public abstract class SerialPortControllerBase {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(SerialPortControllerBase.class);
    /**
     * Serial port
     */
    protected String serialPortDev = "/dev/controller";
    /**
     * Serial port test instance
     */
    protected String serialPortDevTest = "controller_sp";
    /**
     * Baud rate set to communicate through port
     */
    protected int BAUD_RATE = 19200;
    /**
     * Outpus stream for readnig from serial port
     */
    protected OutputStream outputStream;
    /**
     * Input stream
     */
    protected InputStream inputStream;
    /**
     * Serial port instance
     */
    protected SerialPort serialPort;
    /**
     * Serial port close flag, if TRUE port is closed
     */
    protected boolean closeFlag = true;
    /**
     * Listener class fo serial port controller
     */
    protected SerialPortEventListener listener;
    /**
     * 5s timeout
     */
    protected long TIMEOUT_SERIAL = 10000;

    /**
     * Private constructor - singleton instance
     */
    protected SerialPortControllerBase(boolean open, int serialPortParity, int flowControl) {
        if (open) {
            /* Open port */
            openSerialPort(true, serialPortParity, flowControl);
        }
    }

    /**
     * Check if port is available
     *
     * @return
     */
    synchronized public boolean isAvailable() {
        if (serialPort == null) {
            return false;
        }
        return true;
    }

    /**
     * Send request to serial port. It requires that serial port should respond in
     * a short time to get response frame. This is useful when listener mode is disabled.
     * Must be careful when invoking from different threads (some kind of synchronization
     * must be provided). Method will block until there will be response from serial
     * port. For asynchronious communication shouldn't use that method! (sendAsync instead)
     *
     * @param req request object with the frame
     * @return response object or null if failure
     */
    synchronized public SerialResponse send(SerialRequest req) {
        byte[] respFrame = send(req.getFrame());
        if (respFrame == null) {
            return null;
        } else {
            return createResponseObject(respFrame);
        }
    }

    protected abstract SerialResponse createResponseObject(byte[] respFrame);

    /**
     * Send request to serial port. Async m eans that this method will not wait for
     * response from serial port. It may be used when listener mode is enabled.
     * This allows to asynchronious communication. Ass soon as bytes are sent,
     * the method will return
     *
     * @param req
     * @return TRUE if sent otherwise false
     */
    synchronized public boolean sendAsync(SerialRequest req) {
        return sendAsync(req.getFrame());
    }

    /**
     * Send bytes to serial port. It requires that serial port should respond in
     * a short time to get response frame. This is useful when listener mode is disabled.
     * Must be careful when invoking from different threads (some kind of synchronization
     * must be provided). Method will block until there will be response from serial
     * port. For asynchronious communication shouldn't use that method!
     *
     * @param bytes bytes array to send
     * @return array of bytes containing response frame
     */
    public byte[] send(byte[] bytes) {
        if (closeFlag) {
            logger.warn("Trying to send frame but port is closed!");
            return null;
        }
        try {
            logger.debug("Sending frame: [" + StringUtils.getHexString(bytes) + "]");
            outputStream.write(bytes);
            outputStream.flush();
            return readFrame();
        } catch (IOException ex) {
            logger.error("Error while sending frame [" + StringUtils.getHexString(bytes) + "] " + ex, ex);
        }
        return null;
    }

    /**
     * Send bytes to serial port. Async means that this method will not wait for
     * response from serial port. It may be used when listener mode is enabled.
     * This allows to asynchronious communication. Ass soon as bytes are sent,
     * the method will return
     *
     * @param bytes bytes array to send
     * @return TRUE if sent otherwise false
     */
    private boolean sendAsync(byte[] bytes) {
        if (closeFlag) {
            logger.warn("Trying to send frame but port is closed!");
            return false;
        }
        try {
            logger.debug("Sending frame: [" + StringUtils.getHexString(bytes) + "]");
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (IOException ex) {
            logger.error("Error while sending frame [" + StringUtils.getHexString(bytes) + "] " + ex, ex);
        }
        return false;
    }

    /**
     * Read frame from input stream of serial port.
     *
     * @return if frame read with no error return bytes of received frame,
     *         otheriwse return null
     */
    abstract protected byte[] readFrame() throws IOException;

    /**
     * Used actually for closing streams if opened
     */
    public void saveState() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if ((serialPort != null) && (!closeFlag)) {
                serialPort.close();
            }
        } catch (IOException ex) {
            logger.error("Error closing streams, not clean finish up: " + ex, ex);
        }
    }

    public void readState() {
        //Do nothing
    }

    /**
     * Closes serial port
     *
     * @return
     */
    synchronized public boolean closeSerialPort() {
        if (!closeFlag) {
            serialPort.close();
            serialPort = null;
            closeFlag = true;
        }
        return true;
    }

    /**
     * Check if serial port is open
     *
     * @return
     */
    synchronized public boolean isClosed() {
        return closeFlag;
    }

    /**
     * Read setup properties
     */
    protected abstract void setup();

    /**
     * Open serial port if was closed
     *
     * @param reloadProp if TRUE when opening port method will try
     *                   to refresh port settings, otherwise will use previous settings
     * @return TRUE if opened otherwise false
     */
    synchronized public boolean openSerialPort(boolean reloadProp, int serialPortParity, int flowControl) {
        closeFlag = true;
        if (reloadProp) {
            setup();
        }
        try {
            if (false) {
                serialPort = SerialPortFactory.createSerialPort(serialPortDev, true);
            } else {
                serialPort = SerialPortFactory.createSerialPort(serialPortDevTest, false);
            }
            serialPort.setSerialPortParams(BAUD_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    serialPortParity);
            serialPort.setFlowControlMode(flowControl);
            try {
                outputStream = serialPort.getOutputStream();
                /* Set listener mode */
                listener = createPortSerialListener(serialPort);
                serialPort.addEventListener(listener);
                serialPort.notifyOnDataAvailable(true);
                /* Close flag */
                closeFlag = false;
                return true;
            } catch (TooManyListenersException ex) {
                logger.error("Error while attaching listener to serial port! " + ex, ex);
            } catch (IOException ex) {
                logger.error("Error while getting streams to serial port! " + ex, ex);
            }
        } catch (UnsupportedCommOperationException ex) {
            logger.error("Error while creating serial port! " + ex, ex);
        } catch (PortInUseException ex) {
            logger.error("Error during set up serial port:" + ex, ex);
        }
        serialPort = null;
        return false;
    }

    protected abstract SerialPortEventListener createPortSerialListener(SerialPort serialPort);

}
