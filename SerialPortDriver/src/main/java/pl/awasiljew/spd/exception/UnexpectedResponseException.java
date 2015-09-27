package pl.awasiljew.spd.exception;

import pl.awasiljew.spd.data.protocol.ByteFrame;

/**
 * User: Adam
 * Date: 27.09.15
 * Time: 15:02
 */
public class UnexpectedResponseException extends SendFrameException {

    public UnexpectedResponseException(Exception e) {
        super(e);
    }

    public UnexpectedResponseException(Exception e, ByteFrame frame) {
        super(e, frame);
    }

    public UnexpectedResponseException(String message, ByteFrame frame) {
        super(message, frame);
    }
}
