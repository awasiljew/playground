package pl.awasiljew.spd.utils;

import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 * <p>Util class for converting different kind of data, objects etc. to string.
 * Useful for debugging and loggin purposes.</p>
 * @author awasiljew
 */
public class StringUtils {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(StringUtils.class);
    private static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
            (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'
    };

    /**
     * Convert raw data to hex String
     * @param raw
     * @return
     */
    public static String getHexString(byte[] raw) {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        try {
            return new String(hex, "ASCII");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error while converting to HEX " + ex);
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    /**
     * <p>Return byte as HEX string</p>
     * @param b
     * @return
     */
    public static String getHexString(byte b) {
        byte[] hex = new byte[2];
        int v = b & 0xFF;
        hex[0] = HEX_CHAR_TABLE[v >>> 4];
        hex[1] = HEX_CHAR_TABLE[v & 0xF];
        try {
            return new String(hex, "ASCII");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error while converting to HEX " + ex);
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Parse string to byte data
     * @param data
     * @return
     */
    public static byte parseByteBinaryString(String data) {
        byte bh;
        byte bl;
        bh = Byte.parseByte(data.substring(0, 4),2);
        bh = (byte)(bh << 4);
        bl = Byte.parseByte(data.substring(4, 8),2);
        return (byte)(bh | bl);
    }
}

