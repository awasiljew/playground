package pl.awasiljew.spd.data;

/**
 * @author Adam Wasiljew
 */
public class ParsedSerialResponse {

    private boolean dataParsed;
    private int dataConsumed;
    private SerialResponse response;

    public ParsedSerialResponse(boolean dataParsed, int dataConsumed, SerialResponse response) {
        this.dataParsed = dataParsed;
        this.dataConsumed = dataConsumed;
        this.response = response;
    }

    public int getDataConsumed() {
        return dataConsumed;
    }

    public void setDataConsumed(int dataConsumed) {
        this.dataConsumed = dataConsumed;
    }

    public SerialResponse getResponse() {
        return response;
    }

    public void setResponse(SerialResponse response) {
        this.response = response;
    }

    public boolean isDataParsed() {
        return dataParsed;
    }

    public void setDataParsed(boolean dataParsed) {
        this.dataParsed = dataParsed;
    }
}
