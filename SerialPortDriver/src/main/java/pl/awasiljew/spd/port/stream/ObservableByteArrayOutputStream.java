package pl.awasiljew.spd.port.stream;

import pl.awasiljew.spd.port.listener.DataWriteListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Adam Wasiljew
 */
public class ObservableByteArrayOutputStream extends OutputStream {

    private ByteArrayOutputStream byteArrayOutputStream;
    private List<DataWriteListener> dataWriteListenerList;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public ObservableByteArrayOutputStream(ByteArrayOutputStream outputStream) {
        this.byteArrayOutputStream = outputStream;
        this.dataWriteListenerList = new ArrayList<DataWriteListener>();
    }

    @Override
    public void write(int b) throws IOException {
        byteArrayOutputStream.write(b);
        notifyListeners();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byteArrayOutputStream.write(b, off, len);
        notifyListeners();
    }

    @Override
    public void flush() throws IOException {
        byteArrayOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        byteArrayOutputStream.close();
    }

    public void addListener(DataWriteListener dataWriteListener) {
        dataWriteListenerList.add(dataWriteListener);
    }

    private void notifyListeners() {
        for (final DataWriteListener listener : dataWriteListenerList) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.dataWritten();
                }
            });
        }
    }

    public byte[] toByteArray() {
        return byteArrayOutputStream.toByteArray();
    }

    public void reset() {
        byteArrayOutputStream.reset();
    }

}
