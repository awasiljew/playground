package pl.awasiljew.spd.data.protocol;

/**
 * User: Adam
 * Date: 26.09.15
 * Time: 21:28
 */
public interface ByteDataParser {
    IdentifiedByteFrames parse(byte[] data);
    boolean expectedResponse(ByteFrame request, ByteFrame response);
}
