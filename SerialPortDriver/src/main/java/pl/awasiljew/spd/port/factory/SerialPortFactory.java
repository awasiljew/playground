package pl.awasiljew.spd.port.factory;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

/**
 * @author Adam Wasiljew
 */
public class SerialPortFactory {

    private SerialPortInstanceFactory serialPortInstanceFactory;
    private SerialPortSettings serialPortSettings;

    public SerialPortFactory(SerialPortInstanceFactory serialPortInstanceFactory, SerialPortSettings serialPortSettings) {
        this.serialPortInstanceFactory = serialPortInstanceFactory;
        this.serialPortSettings = serialPortSettings;
    }

    public SerialPort createSerialPort() throws UnsupportedCommOperationException, PortInUseException {
        SerialPort serialPort = serialPortInstanceFactory.createInstance(serialPortSettings);
        serialPort.setSerialPortParams(serialPortSettings.getBaudRate(),
                serialPortSettings.getDataBits().getValue(),
                serialPortSettings.getStopBits().getValue(),
                serialPortSettings.getParity().getValue());
        serialPort.setFlowControlMode(serialPortSettings.getFlowControl().getValue());
        return serialPort;
    }

}
