package pl.awasiljew.spd.data.protocol;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * User: Adam
 * Date: 26.09.15
 * Time: 21:29
 */
public class IdentifiedByteFrames {
    private final List<ByteFrame> frames = newArrayList();
    int length = 0;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<ByteFrame> getFrames() {
        return frames;
    }

    public void addFrame(ByteFrame frame) {
        frames.add(frame);
    }
}
