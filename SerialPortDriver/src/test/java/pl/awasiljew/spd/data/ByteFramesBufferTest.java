package pl.awasiljew.spd.data;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.awasiljew.spd.data.buffers.ByteFramesBuffer;
import pl.awasiljew.spd.data.buffers.ByteFramesBufferConfig;
import pl.awasiljew.spd.data.protocol.ByteDataParser;
import pl.awasiljew.spd.data.protocol.ByteFrame;
import pl.awasiljew.spd.data.protocol.IdentifiedByteFrames;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * User: Adam
 * Date: 26.09.15
 * Time: 21:31
 */
public class ByteFramesBufferTest {

    private static final byte START_BYTE = 'x';
    private ByteFramesBuffer byteFramesBuffer;
    private ExecutorService executorService;

    @BeforeMethod
    public void setUp() throws Exception {
        executorService = newFixedThreadPool(4);
        ByteFramesBufferConfig config = new ByteFramesBufferConfig();
        config.setPollTimeout(300);
        byteFramesBuffer = new ByteFramesBuffer(customFrameParser(), config);
    }

    @Test
    public void shouldIdentifyFrame() {
        // Given
        String data = "x4Adam";
        sendDataToBuffer(data);
        // When
        ByteFrame lastFrame = pollLastFrame();
        // Then
        assertDataEqualsFrame(data, lastFrame);
    }

    @Test
    public void shouldIdentifyFrameAndSkipPrefix() {
        // Given
        String prefix = "someUselessPrefix";
        String data = "x4Adam";
        String fullStream = prefix + data;
        sendDataToBuffer(fullStream);
        // When
        ByteFrame lastFrame = pollLastFrame();
        // Then
        assertDataEqualsFrame(data, lastFrame);
    }

    @Test
    public void shouldIdentifyFrameWithUselessSuffix() {
        // Given
        String data = "x4Adam";
        String suffix = "someUselessSuffix";
        String fullStream = data + suffix;
        sendDataToBuffer(fullStream);
        // When
        ByteFrame lastFrame = pollLastFrame();
        // Then
        assertDataEqualsFrame(data, lastFrame);
    }

    @Test
    public void shouldIdentifyTwoConsecutiveFrames() {
        // Given
        String data1 = "x4Adam";
        String data2 = "x4Mati";
        String fullStream = data1 + data2;
        sendDataToBuffer(fullStream);
        // When
        ByteFrame frame1 = pollLastFrame();
        ByteFrame frame2 = pollLastFrame();
        // Then
        assertDataEqualsFrame(data1, frame1);
        assertDataEqualsFrame(data2, frame2);
    }

    @Test
    public void shouldIdentifyTwoFramesWithSkippedBytesInBetween() {
        // Given
        String data1 = "x4Adam";
        String garbage = "someGarbage";
        String data2 = "x4Mati";
        String fullStream = data1 + garbage + data2;
        sendDataToBuffer(fullStream);
        // When
        ByteFrame frame1 = pollLastFrame();
        ByteFrame frame2 = pollLastFrame();
        // Then
        assertDataEqualsFrame(data1, frame1);
        assertDataEqualsFrame(data2, frame2);
    }

    @Test
    public void shouldReturnNullWhenNoFrameIsAvailable() {
        // When
        ByteFrame frame = pollLastFrame();
        // Then
        assertNull(frame);
    }

    @Test
    public void shouldReturnFrameAfterAllDataCollected() {
        // Given
        String chunk1 = "x4";
        String chunk2 = "Adam";
        String fullFrame = chunk1 + chunk2;
        // When
        sendDataToBuffer(chunk1);
        // Then
        assertNull(pollLastFrame());
        // When
        sendDataToBuffer(chunk2);
        // Them
        assertDataEqualsFrame(fullFrame, pollLastFrame());
    }

    @Test
    public void shouldWaitForConcurrentThreadFinishSendingData() {
        // Given
        final String data = "x4Adam";
        sendDataToBufferAsync(data);
        // When
        ByteFrame lastFrame = pollLastFrame();
        // Then
        assertDataEqualsFrame(data, lastFrame);
    }

    @Test
    public void shouldClearBufferFromFrames() {
        // Given
        String data = "x4Adam";
        sendDataToBuffer(data);
        // When
        byteFramesBuffer.reset();
        ByteFrame lastFrame = pollLastFrame();
        // Then
        assertNull(lastFrame);
    }

    @Test
    public void shouldClearBufferFromFramesButLeavingInternalStreamWithSuffixBytes() {
        // Given
        String data1 = "x4Adam";
        String data2 = "x4";
        String data3 = "Mati";
        sendDataToBuffer(data1);
        sendDataToBuffer(data2);
        // When
        byteFramesBuffer.reset();
        // Then
        ByteFrame lastFrame = pollLastFrame();
        assertNull(lastFrame);
        // When
        sendDataToBuffer(data3);
        lastFrame = pollLastFrame();
        // Then
        assertDataEqualsFrame(data2 + data3, lastFrame);
    }

    private void sendDataToBufferAsync(final String data) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Ignore
                }
                sendDataToBuffer(data);
            }
        });
    }

    private void assertDataEqualsFrame(String data, ByteFrame lastFrame) {
        assertEquals(data, new String(lastFrame.getFrame()));
    }

    private ByteFrame pollLastFrame() {
        return byteFramesBuffer.getLastFrame();
    }

    private void sendDataToBuffer(String data) {
        byteFramesBuffer.receive(data.getBytes());
    }

    // Custom frame parser shows how the real parser should work.
    // Here we assume start byte, then length byte, then data content.
    // When frame is identified, it's added to list of frames.
    // Parsing is performed until last byte is checked!
    private ByteDataParser customFrameParser() {
        return new ByteDataParser() {
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
                // It's no matter what request/response pair is for byte frames buffer
                return true;
            }

            private byte[] getFrame(byte[] data, int i, int length) {
                byte[] frame = new byte[length + 2];
                System.arraycopy(data, i, frame, 0, frame.length);
                return frame;
            }

            private int getFrameDataLength(byte[] data, int i) {
                return Integer.parseInt("" + (char) data[i + 1]);
            }
        };
    }

}
