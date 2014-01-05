package pl.awasiljew.spd.port.settings;

import gnu.io.SerialPort;

/**
 * @author Adam Wasiljew
 */
public enum FlowControl {

    NONE(SerialPort.FLOWCONTROL_NONE),
    RTSCTS_IN(SerialPort.FLOWCONTROL_RTSCTS_IN),
    RTSCTS_OUT(SerialPort.FLOWCONTROL_RTSCTS_OUT),
    XONXOFF_IN(SerialPort.FLOWCONTROL_XONXOFF_IN),
    XONXOFF_OUT(SerialPort.FLOWCONTROL_XONXOFF_OUT);

    private int value;

    private FlowControl(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
