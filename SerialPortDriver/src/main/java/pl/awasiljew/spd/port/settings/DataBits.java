package pl.awasiljew.spd.port.settings;

import gnu.io.SerialPort;

/**
 * @author Adam Wasiljew
 */
public enum DataBits {

    BITS_8(SerialPort.DATABITS_8),
    BITS_7(SerialPort.DATABITS_7),
    BITS_6(SerialPort.DATABITS_6),
    BITS_5(SerialPort.DATABITS_5);

    private int value;

    private DataBits(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
