package pl.awasiljew.spd.data.protocol;

import java.util.Arrays;

/**
 * User: Adam
 * Date: 26.09.15
 * Time: 20:38
 */
public class ByteFrame {

    private final byte[] frame;

    public ByteFrame(byte[] frame) {
        this.frame = frame;
    }

    public byte[] getFrame() {
        return frame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteFrame)) return false;
        ByteFrame byteFrame = (ByteFrame) o;
        if (!Arrays.equals(frame, byteFrame.frame)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return frame != null ? Arrays.hashCode(frame) : 0;
    }
}
