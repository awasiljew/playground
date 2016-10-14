package pl.awasiljew.tasks;

import org.apache.log4j.Logger;

/**
 * @author Adam Wasiljew
 */
public class LongRunningTask implements Task {

    private static final Logger log = Logger.getLogger(LongRunningTask.class);

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        log.info("LongRunningTask DONE!");
    }

}
