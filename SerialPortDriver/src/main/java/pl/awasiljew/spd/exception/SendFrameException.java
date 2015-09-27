package pl.awasiljew.spd.exception;

import pl.awasiljew.spd.data.protocol.ByteFrame;

/**
 * @author Adam Wasiljew
 */
public class SendFrameException extends Exception {
    private final ByteFrame frame;

    public SendFrameException(String message, ByteFrame frame) {
        super(message);
        this.frame = frame;
    }

    public SendFrameException(Exception e) {
        super(e);
        frame = null;
    }

    public SendFrameException(Exception e, ByteFrame frame) {
        super(e);
        this.frame = frame;
    }

    public ByteFrame getFrame() {
        return frame;
    }
}
