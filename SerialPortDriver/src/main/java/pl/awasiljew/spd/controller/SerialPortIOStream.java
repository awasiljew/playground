package pl.awasiljew.spd.controller;

import gnu.io.SerialPort;
import org.apache.log4j.Logger;
import pl.awasiljew.spd.data.*;
import pl.awasiljew.spd.port.settings.SerialPortSettings;
import pl.awasiljew.spd.utils.HexDecoder;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam Wasiljew
 */
public class SerialPortIOStream implements SerialDataConsumer {

    private static Logger log = Logger.getLogger(SerialPortIOStream.class);
    private BlockingQueue<SerialResponse> responseBlockingQueue = new ArrayBlockingQueue<SerialResponse>(1);
    private SerialPort serialPort;
    private SerialResponseFactory serialResponseFactory;
    private long portTimeoutInMs;

    public SerialPortIOStream(SerialPort serialPort, SerialResponseFactory serialResponseFactory, SerialPortSettings serialPortSettings) {
        this.serialPort = serialPort;
        this.serialResponseFactory = serialResponseFactory;
        this.portTimeoutInMs = serialPortSettings.getPortTimeout();
    }

    public SerialResponse send(SerialRequest req) throws IOException, InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Sending frame: [" + HexDecoder.getHexString(req.getFrame()) + "]");
        }
        serialPort.getOutputStream().write(req.getFrame());
        serialPort.getOutputStream().flush();
        return responseBlockingQueue.poll(portTimeoutInMs, TimeUnit.MILLISECONDS);
    }

    public void sendAsync(SerialRequest req) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending frame: [" + HexDecoder.getHexString(req.getFrame()) + "]");
        }
        serialPort.getOutputStream().write(req.getFrame());
        serialPort.getOutputStream().flush();
    }

    @Override
    public int consume(byte[] data) {
        ParsedSerialResponse parsedSerialResponse = serialResponseFactory.buildResponse(data);
        if (parsedSerialResponse.isDataParsed()) {
            try {
                responseBlockingQueue.put(parsedSerialResponse.getResponse());
            } catch (InterruptedException e) {
                log.error(e, e);
                reset();
            }
        }
        return parsedSerialResponse.getDataConsumed();
    }

    public void reset() {
        responseBlockingQueue.clear();
    }

}
