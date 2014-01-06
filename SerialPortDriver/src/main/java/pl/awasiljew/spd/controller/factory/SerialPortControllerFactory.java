package pl.awasiljew.spd.controller.factory;

import pl.awasiljew.spd.controller.impl.BaseSerialPortController;
import pl.awasiljew.spd.controller.SerialPortController;
import pl.awasiljew.spd.data.SerialResponseFactory;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Wasiljew
 */
public class SerialPortControllerFactory {

    private static Map<SerialPortSettings, SerialPortController> controllerMap = new HashMap<SerialPortSettings, SerialPortController>();

    public static SerialPortController buildSerialPortController(SerialPortSettings serialPortSettings, SerialPortInstanceFactory serialPortInstanceFactory, SerialResponseFactory serialResponseFactory) {
        if(!controllerMap.containsKey(serialPortSettings)) {
            synchronized (SerialPortControllerFactory.class) {
                if(!controllerMap.containsKey(serialPortSettings)) {
                    controllerMap.put(serialPortSettings, new BaseSerialPortController(serialPortSettings, serialPortInstanceFactory, serialResponseFactory));
                }
            }
        }
        return controllerMap.get(serialPortSettings);
    }

}
