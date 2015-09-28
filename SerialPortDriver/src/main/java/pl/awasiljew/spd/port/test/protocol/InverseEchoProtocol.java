package pl.awasiljew.spd.port.test.protocol;

import pl.awasiljew.spd.data.protocol.ByteDataParser;
import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.data.protocol.IdentifiedByteFrames;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;

/**
 * User: Adam
 * Date: 28.09.15
 * Time: 22:13
 */
public class InverseEchoProtocol implements ByteDataParser, EmulatedSerialPort.DataReceiver {

    private EmulatedSerialPort serialPort;

    public static final byte START_BYTE = 'x';
    public static final byte[] UNSUPPORTED_MESSAGE = "UnsupportedMessage".getBytes();


    public InverseEchoProtocol(EmulatedSerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPort.setDataReceiver(this);
    }

    @Override
    public IdentifiedByteFrames parse(byte[] data) {
        IdentifiedByteFrames frames = new IdentifiedByteFrames();
        int i = 0;
        while (i < data.length) {
            if (data[i] == START_BYTE) { // We found start
                if (i + 1 < data.length) {
                    try {  // Try to get rest of frame
                        int length = getFrameDataLength(data, i);
                        if (i + 1 + length < data.length) { // We have a full frame available
                            int end = i + 1 + length;
                            frames.addFrame(new ByteFrame(getFrame(data, i, length)));
                            frames.setLength(end + 1);
                            i = end + 1;
                        } else {
                            i++;
                        }
                    } catch (NumberFormatException ex) {
                        i++;
                    }
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
        return frames;
    }

    @Override
    public boolean expectedResponse(ByteFrame request, ByteFrame response) {
        byte[] requestFrame = request.getFrame();
        byte[] responseFrame = response.getFrame();
        if (requestFrame != null && responseFrame != null) {
            if (requestFrame.length == requestFrame.length) {
                for (int i = 2; i < requestFrame.length; i++) {
                    if (requestFrame[i] != responseFrame[responseFrame.length - i + 1]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private byte[] getFrame(byte[] data, int i, int length) {
        byte[] frame = new byte[length + 2];
        System.arraycopy(data, i, frame, 0, frame.length);
        return frame;
    }

    private int getFrameDataLength(byte[] data, int i) {
        return Integer.parseInt("" + (char) data[i + 1]);
    }

    @Override
    public void receive(byte[] data) {
        if (new String(data).equals(new String(UNSUPPORTED_MESSAGE))) {
            serialPort.dataReady("x1U".getBytes());
        } else {
            IdentifiedByteFrames identifiedByteFrames = parse(data);
            for (ByteFrame frame : identifiedByteFrames.getFrames()) {
                byte[] requestFrame = frame.getFrame();
                byte[] newFrame = new byte[requestFrame.length];
                newFrame[0] = requestFrame[0];
                newFrame[1] = requestFrame[1];
                for (int i = 2; i < requestFrame.length; i++) {
                    newFrame[i] = requestFrame[requestFrame.length - i + 1];
                }
                serialPort.dataReady(newFrame);
            }
        }
    }
}
