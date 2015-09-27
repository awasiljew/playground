package pl.awasiljew.spd.data.buffers;

import pl.awasiljew.spd.data.protocol.ByteFrame;

/**
 * User: Adam
 * Date: 27.09.15
 * Time: 15:14
 */
public interface SerialDataFramesBuffer {

    ByteFrame getLastFrame();

    boolean expectedFrame(ByteFrame req, ByteFrame resp);

    void reset();

}
