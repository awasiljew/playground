package pl.awasiljew.spd.port;

import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;

/**
 * @author Adam Wasiljew
 */
public abstract class SerialPortFactory {

    public static SerialPort createSerialPort(String portFileName, boolean hardware) throws PortInUseException {
        if (hardware) {
            return new RXTXPort(portFileName);
        } else {
            return new EmulatedSerialPort();
        }
    }

}
