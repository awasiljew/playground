package pl.awasiljew.spd.controller.factory;

import pl.awasiljew.spd.controller.SerialPortController;
import pl.awasiljew.spd.controller.impl.SerialPortControllerImpl;
import pl.awasiljew.spd.data.buffers.ByteFramesBuffer;
import pl.awasiljew.spd.data.buffers.ByteFramesBufferConfig;
import pl.awasiljew.spd.data.protocol.ByteDataParser;
import pl.awasiljew.spd.port.factory.SerialPortInstanceFactory;
import pl.awasiljew.spd.port.settings.SerialPortSettings;

/**
 * @author Adam Wasiljew
 */
public class SerialPortControllerFactory {

    public SerialPortController createInstance(ByteDataParser dataParser, SerialPortInstanceFactory serialPortInstanceFactory, SerialPortSettings serialPortSettings) {
        ByteFramesBufferConfig config = new ByteFramesBufferConfig();
        ByteFramesBuffer framesBuffer = new ByteFramesBuffer(dataParser, config);
        return new SerialPortControllerImpl(
                serialPortSettings,
                serialPortInstanceFactory,
                framesBuffer,
                framesBuffer
        );
    }

}
