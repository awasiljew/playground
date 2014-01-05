package pl.awasiljew.spd.port.settings;

import gnu.io.SerialPort;

/**
 * @author Adam Wasiljew
 */
public enum StopBits {

    BITS_1(SerialPort.STOPBITS_1),
    BITS_1_5(SerialPort.STOPBITS_1_5),
    BITS_2(SerialPort.STOPBITS_2);

    private int value;

    private StopBits(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
