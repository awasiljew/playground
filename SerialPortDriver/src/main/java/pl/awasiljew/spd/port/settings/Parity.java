package pl.awasiljew.spd.port.settings;

import gnu.io.SerialPort;

/**
 * @author Adam Wasiljew
 */
public enum Parity {

    EVEN(SerialPort.PARITY_EVEN),
    MARK(SerialPort.PARITY_MARK),
    NONE(SerialPort.PARITY_NONE),
    ODD(SerialPort.PARITY_ODD),
    SPACE(SerialPort.PARITY_SPACE);

    private int value;

    private Parity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
