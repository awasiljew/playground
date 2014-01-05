package pl.awasiljew.spd.port.factory;

import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

/**
 * @author Adam Wasiljew
 */
public class RXTXSerialPortInstanceFactory implements SerialPortInstanceFactory {
    @Override
    public SerialPort createInstance(SerialPortSettings settings) throws PortInUseException {
        return new RXTXPort(settings.getPortName());
    }
}
