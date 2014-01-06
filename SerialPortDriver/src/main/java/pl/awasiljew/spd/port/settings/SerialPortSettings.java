package pl.awasiljew.spd.port.settings;

/**
 * @author Adam Wasiljew
 */
public class SerialPortSettings {

    private DataBits dataBits;
    private FlowControl flowControl;
    private Parity parity;
    private StopBits stopBits;
    private int baudRate;
    private String portName;
    private long portTimeout;

    public SerialPortSettings(SerialPortSettings settings) {
        this.dataBits = settings.getDataBits();
        this.flowControl = settings.getFlowControl();
        this.parity = settings.getParity();
        this.stopBits = settings.getStopBits();
        this.baudRate = settings.getBaudRate();
        this.portName = settings.getPortName();
        this.portTimeout = settings.getPortTimeout();
    }

    public SerialPortSettings(DataBits dataBits, FlowControl flowControl, Parity parity, StopBits stopBits, int baudRate, String portName, int portTimeout) {
        this.dataBits = dataBits;
        this.flowControl = flowControl;
        this.parity = parity;
        this.stopBits = stopBits;
        this.baudRate = baudRate;
        this.portName = portName;
        this.portTimeout = portTimeout;
    }

    public DataBits getDataBits() {
        return dataBits;
    }

    public void setDataBits(DataBits dataBits) {
        this.dataBits = dataBits;
    }

    public FlowControl getFlowControl() {
        return flowControl;
    }

    public void setFlowControl(FlowControl flowControl) {
        this.flowControl = flowControl;
    }

    public Parity getParity() {
        return parity;
    }

    public void setParity(Parity parity) {
        this.parity = parity;
    }

    public StopBits getStopBits() {
        return stopBits;
    }

    public void setStopBits(StopBits stopBits) {
        this.stopBits = stopBits;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public long getPortTimeout() {
        return portTimeout;
    }

    public void setPortTimeout(long portTimeout) {
        this.portTimeout = portTimeout;
    }
}
