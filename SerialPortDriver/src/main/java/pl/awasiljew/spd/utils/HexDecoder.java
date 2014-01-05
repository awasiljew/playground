package pl.awasiljew.spd.utils;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

/**
 * <p>Util class for converting different kind of data, objects etc. to string.
 * Useful for debugging and loggin purposes.</p>
 *
 * @author awasiljew
 */
public class HexDecoder {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(HexDecoder.class);
    private static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
            (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'
    };

    /**
     * Convert raw data to hex String
     *
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

}

