package pl.awasiljew.spd.data.buffers;

import java.util.concurrent.TimeUnit;

/**
 * User: Adam
 * Date: 26.09.15
 * Time: 22:01
 */
public class ByteFramesBufferConfig {

    private long pollTimeout = 5000;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public long getPollTimeout() {
        return pollTimeout;
    }

    public void setPollTimeout(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
