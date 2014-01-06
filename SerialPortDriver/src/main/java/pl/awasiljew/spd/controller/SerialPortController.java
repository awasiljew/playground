package pl.awasiljew.spd.controller;

import pl.awasiljew.spd.data.SerialRequest;
import pl.awasiljew.spd.data.SerialResponse;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;

/**
 * @author Adam Wasiljew
 */
public interface SerialPortController {

    boolean isOpen();

    SerialResponse send(SerialRequest req) throws PortClosedException, SendFrameException;

    void sendAsync(SerialRequest req) throws PortClosedException, SendFrameException;

    void close();

    void open();
}
