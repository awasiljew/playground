package pl.awasiljew.spd.data.buffers;

import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.data.protocol.IdentifiedByteFrames;
import pl.awasiljew.spd.data.SerialDataReceiver;
import pl.awasiljew.spd.data.protocol.ByteDataParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Adam
 * Date: 25.09.15
 * Time: 16:19
 */
public class ByteFramesBuffer implements SerialDataReceiver, SerialDataFramesBuffer {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
    private final BlockingQueue<ByteFrame> frames = new LinkedBlockingQueue<ByteFrame>();
    private final ByteDataParser parser;
    private final ByteFramesBufferConfig config;

    public ByteFramesBuffer(ByteDataParser parser, ByteFramesBufferConfig config) {
        this.parser = parser;
        this.config = config;
    }

    @Override
    public synchronized void receive(byte[] data) {
        writeToStream(data);
        parseByteFrames();
    }

    @Override
    public ByteFrame getLastFrame() {
        try {
            return frames.poll(config.getPollTimeout(), config.getTimeUnit());
        } catch (InterruptedException e) {
            // What to do???
        }
        return null;
    }

    @Override
    public boolean expectedFrame(ByteFrame req, ByteFrame resp) {
        return parser.expectedResponse(req, resp);
    }

    @Override
    public void reset() {
        frames.clear();
    }

    private void parseByteFrames() {
        byte[] currentData = outputStream.toByteArray();
        IdentifiedByteFrames identifiedByteFrames = parser.parse(currentData);
        for (ByteFrame frame : identifiedByteFrames.getFrames()) {
            frames.add(frame);
        }
        int length = identifiedByteFrames.getLength();
        if (length > 0) {
            byte[] remaining = new byte[currentData.length - length];
            System.arraycopy(currentData, length, remaining, 0, remaining.length);
            outputStream.reset();
            writeToStream(remaining);
        }
    }

    private void writeToStream(byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            // What to do???
        }
    }
}
