package pl.awasiljew.spd.data;

/**
 * @author Adam Wasiljew
 */
public interface SerialResponseFactory {

    ParsedSerialResponse buildResponse(byte [] data);

}
