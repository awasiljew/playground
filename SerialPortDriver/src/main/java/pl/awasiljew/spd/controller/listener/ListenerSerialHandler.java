package pl.awasiljew.spd.controller.listener;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.apache.log4j.Logger;
import pl.awasiljew.spd.data.SerialDataConsumer;
import pl.awasiljew.spd.utils.HexDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ListenerSerialHandler implements SerialPortEventListener {

    private static final int BUF_LEN = 132;
    private byte[] buffer;
    private ByteArrayOutputStream streamBuffer;
    private SerialPort serialPort;
    private SerialDataConsumer serialDataConsumer;
    private static final Logger log = Logger.getLogger(ListenerSerialHandler.class);

    public ListenerSerialHandler(SerialPort serialPort, SerialDataConsumer serialDataConsumer) {
        this.buffer = new byte[BUF_LEN];
        this.streamBuffer = new ByteArrayOutputStream();
        this.streamBuffer.reset();
        this.serialPort = serialPort;
        this.serialDataConsumer = serialDataConsumer;
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
                        sendDataToConsumer();
                        break;
                }
            } catch (IOException ex) {
                log.error("Error while reading from serial port!", ex);
            }
        }
    }

    private void readFromStream() throws IOException {
        int bytes = serialPort.getInputStream().read(buffer);
        int count = 0;
        while (bytes > 0) {
            count += bytes;
            streamBuffer.write(buffer, 0, bytes);
            bytes = serialPort.getInputStream().read(buffer);
        }
        if (log.isDebugEnabled()) {
            log.debug("Received " + count + " bytes data: [" + HexDecoder.getHexString(streamBuffer.toByteArray()) + "]");
        }
    }

    private void sendDataToConsumer() {
        int received = serialDataConsumer.consume(streamBuffer.toByteArray());
        if (received > 0) {
            byte[] arr = streamBuffer.toByteArray();
            streamBuffer.reset();
            streamBuffer.write(arr, received, arr.length - received);
        }
    }

}
