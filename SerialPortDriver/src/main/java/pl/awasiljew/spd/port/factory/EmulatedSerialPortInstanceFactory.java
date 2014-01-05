package pl.awasiljew.spd.port.factory;

import gnu.io.SerialPort;
import pl.awasiljew.spd.port.settings.SerialPortSettings;
import pl.awasiljew.spd.port.test.EmulatedSerialPort;

/**
 * @author Adam Wasiljew
 */
public class EmulatedSerialPortInstanceFactory implements SerialPortInstanceFactory {
    @Override
    public SerialPort createInstance(SerialPortSettings settings) {
        return new EmulatedSerialPort();
    }
}
