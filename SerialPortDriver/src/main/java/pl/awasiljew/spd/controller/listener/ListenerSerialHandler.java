package pl.awasiljew.spd.controller.listener;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.awasiljew.spd.data.SerialDataReceiver;
import pl.awasiljew.spd.utils.HexDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ListenerSerialHandler implements SerialPortEventListener {

    protected static final int BUF_LEN = 132;
    private byte[] buffer;
    private ByteArrayOutputStream streamBuffer;
    private SerialPort serialPort;
    private SerialDataReceiver serialDataReceiver;
    private static final Logger log = LoggerFactory.getLogger(ListenerSerialHandler.class);

    public ListenerSerialHandler(SerialPort serialPort, SerialDataReceiver serialDataReceiver) {
        this.buffer = new byte[BUF_LEN];
        this.streamBuffer = new ByteArrayOutputStream();
        this.streamBuffer.reset();
        this.serialPort = serialPort;
        this.serialDataReceiver = serialDataReceiver;
    }

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
                        readFromStream();
                        break;
                }
            } catch (IOException ex) {
                log.error("Error while reading from serial port!", ex);
            }
        }
    }

    private void readFromStream() throws IOException {
        int bytes = readBytes();
        int count = 0;
        while (bytes > 0) {
            count += bytes;
            writeToStreamBuffer(bytes);
            bytes = readBytes();
        }
        if (count > 0) {
            byte[] rawData = streamBuffer.toByteArray();
            logRawData(count, rawData);
            serialDataReceiver.receive(rawData);
            streamBuffer.reset();
        }
    }

    private void logRawData(int count, byte[] rawData) {
        if (log.isDebugEnabled()) {
            log.debug("Received " + count + " bytes data: [" + HexDecoder.getHexString(rawData) + "]");
        }
    }

    private void writeToStreamBuffer(int bytes) {
        streamBuffer.write(buffer, 0, bytes);
    }

    private int readBytes() throws IOException {
        return serialPort.getInputStream().read(buffer);
    }

}
