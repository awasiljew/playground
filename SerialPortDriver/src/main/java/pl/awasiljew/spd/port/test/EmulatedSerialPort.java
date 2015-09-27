package pl.awasiljew.spd.port.test;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>Test serial port</p>
 *
 * @author Adam Wasiljew
 */
public class EmulatedSerialPort extends SerialPort {

    private static Logger log = LoggerFactory.getLogger(EmulatedSerialPort.class);
    private static final int BUF_SIZE = 1024;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;
    private int flowControlMode;
    private boolean dtr;
    private boolean rts;
    private boolean ri;
    private SerialPortEventListener listener;
    private byte parityErrorChar;
    private byte endOfInputChar;
    private String uartType;
    private int baudBase;
    private int divisor;
    private boolean lowLatency;
    private boolean callOutHangup;
    private int inputBufferSize;
    private int outputBufferSize;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    public EmulatedSerialPort() {
        pipedOutputStream = new PipedOutputStream();
        try {
            pipedInputStream = new PipedInputStream(pipedOutputStream, BUF_SIZE) {
                @Override
                public int read(byte[] b) throws IOException {
                    // This fixes piped input stream, to behave as serial port input stream.
                    // When using serial port input stream, reading is performed until last
                    // bytes are available. When all data was received, method does not block,
                    // instead it returns 0, without filling out input byte array.
                    if (available() > 0) {
                        return super.read(b);
                    }
                    return 0;
                }
            };
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void setSerialPortParams(int i, int i1, int i2, int i3) throws UnsupportedCommOperationException {
        log.debug("Set serial port parameters: " + i + ", " + i1 + ", " + i2 + ", " + i3);
        baudRate = i;
        dataBits = i1;
        stopBits = i2;
        parity = i3;
    }

    @Override
    public int getBaudRate() {
        return baudRate;
    }

    @Override
    public int getDataBits() {
        return dataBits;
    }

    @Override
    public int getStopBits() {
        return stopBits;
    }

    @Override
    public int getParity() {
        return parity;
    }

    @Override
    public void setFlowControlMode(int i) throws UnsupportedCommOperationException {
        flowControlMode = i;
    }

    @Override
    public int getFlowControlMode() {
        return flowControlMode;
    }

    @Override
    public boolean isDTR() {
        return dtr;
    }

    @Override
    public void setDTR(boolean bln) {
        dtr = bln;
    }

    @Override
    public void setRTS(boolean bln) {
        rts = bln;
    }

    @Override
    public boolean isCTS() {
        return !rts;
    }

    @Override
    public boolean isDSR() {
        return !dtr;
    }

    @Override
    public boolean isCD() {
        return !ri;
    }

    @Override
    public boolean isRI() {
        return ri;
    }

    @Override
    public boolean isRTS() {
        return rts;
    }

    @Override
    public void sendBreak(int i) {
        log.debug("Send break: " + i);
    }

    @Override
    public void addEventListener(SerialPortEventListener sl) throws TooManyListenersException {
        if (listener == null) {
            listener = sl;
        } else {
            throw new TooManyListenersException();
        }
    }

    @Override
    public void removeEventListener() {
        listener = null;
    }

    @Override
    public void notifyOnDataAvailable(boolean bln) {

    }

    @Override
    public void notifyOnOutputEmpty(boolean bln) {

    }

    @Override
    public void notifyOnCTS(boolean bln) {

    }

    @Override
    public void notifyOnDSR(boolean bln) {

    }

    @Override
    public void notifyOnRingIndicator(boolean bln) {

    }

    @Override
    public void notifyOnCarrierDetect(boolean bln) {

    }

    @Override
    public void notifyOnOverrunError(boolean bln) {

    }

    @Override
    public void notifyOnParityError(boolean bln) {

    }

    @Override
    public void notifyOnFramingError(boolean bln) {

    }

    @Override
    public void notifyOnBreakInterrupt(boolean bln) {

    }

    @Override
    public byte getParityErrorChar() throws UnsupportedCommOperationException {
        return parityErrorChar;
    }

    @Override
    public boolean setParityErrorChar(byte b) throws UnsupportedCommOperationException {
        parityErrorChar = b;
        return true;
    }

    @Override
    public byte getEndOfInputChar() throws UnsupportedCommOperationException {
        return endOfInputChar;
    }

    @Override
    public boolean setEndOfInputChar(byte b) throws UnsupportedCommOperationException {
        endOfInputChar = b;
        return true;
    }

    @Override
    public boolean setUARTType(String string, boolean bln) throws UnsupportedCommOperationException {
        uartType = string;
        return bln;
    }

    @Override
    public String getUARTType() throws UnsupportedCommOperationException {
        return uartType;
    }

    @Override
    public boolean setBaudBase(int i) throws UnsupportedCommOperationException, IOException {
        baudBase = i;
        return true;
    }

    @Override
    public int getBaudBase() throws UnsupportedCommOperationException, IOException {
        return baudBase;
    }

    @Override
    public boolean setDivisor(int i) throws UnsupportedCommOperationException, IOException {
        divisor = i;
        return true;
    }

    @Override
    public int getDivisor() throws UnsupportedCommOperationException, IOException {
        return divisor;
    }

    @Override
    public boolean setLowLatency() throws UnsupportedCommOperationException {
        lowLatency = true;
        return lowLatency;
    }

    @Override
    public boolean getLowLatency() throws UnsupportedCommOperationException {
        return lowLatency;
    }

    @Override
    public boolean setCallOutHangup(boolean bln) throws UnsupportedCommOperationException {
        callOutHangup = bln;
        return bln;
    }

    @Override
    public boolean getCallOutHangup() throws UnsupportedCommOperationException {
        return callOutHangup;
    }

    @Override
    public void enableReceiveFraming(int i) throws UnsupportedCommOperationException {
        log.debug("enableReceiveFraming " + i);
    }

    @Override
    public void disableReceiveFraming() {
        log.debug("disableReceiveFraming");
    }

    @Override
    public boolean isReceiveFramingEnabled() {
        return false;
    }

    @Override
    public int getReceiveFramingByte() {
        return 0;
    }

    @Override
    public void disableReceiveTimeout() {
        log.debug("disableReceiveTimeout");
    }

    @Override
    public void enableReceiveTimeout(int i) throws UnsupportedCommOperationException {
        log.debug("enableReceiveTimeout " + i);
    }

    @Override
    public boolean isReceiveTimeoutEnabled() {
        return false;
    }

    @Override
    public int getReceiveTimeout() {
        return 0;
    }

    @Override
    public void enableReceiveThreshold(int i) throws UnsupportedCommOperationException {
        log.debug("enableReceiveThreshold " + i);
    }

    @Override
    public void disableReceiveThreshold() {
        log.debug("disableReceiveThreshold");
    }

    @Override
    public int getReceiveThreshold() {
        return 0;
    }

    @Override
    public boolean isReceiveThresholdEnabled() {
        return false;
    }

    @Override
    public void setInputBufferSize(int i) {
        inputBufferSize = i;
    }

    @Override
    public int getInputBufferSize() {
        return inputBufferSize;
    }

    @Override
    public void setOutputBufferSize(int i) {
        outputBufferSize = i;
    }

    @Override
    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return pipedInputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return pipedOutputStream;
    }

    @Override
    public void close() {
        // TODO: fix me
        // Right now do nothing
    }

    public Future<?> dataReady(byte[] data) {
        try {
            pipedOutputStream.write(data);
            final SerialPort instance = this;
            return executorService.submit(new Runnable() {
                @Override
                public void run() {
                    listener.serialEvent(new SerialPortEvent(instance, SerialPortEvent.DATA_AVAILABLE, true, true));
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
