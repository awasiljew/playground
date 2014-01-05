package pl.awasiljew.spd.port.factory;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

/**
 * @author Adam Wasiljew
 */
public interface SerialPortInstanceFactory {

    SerialPort createInstance(SerialPortSettings settings) throws PortInUseException;

}
