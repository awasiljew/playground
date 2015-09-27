package pl.awasiljew.spd.controller;

import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.exception.PortClosedException;
import pl.awasiljew.spd.exception.SendFrameException;

/**
 * @author Adam Wasiljew
 */
public interface SerialPortController {

    boolean isOpen();

    ByteFrame send(ByteFrame req) throws PortClosedException, SendFrameException;

    void sendAsync(ByteFrame req) throws PortClosedException, SendFrameException;

    void close();

    void open();
}
