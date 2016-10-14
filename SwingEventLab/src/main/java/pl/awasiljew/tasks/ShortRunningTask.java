package pl.awasiljew.tasks;

import org.apache.log4j.Logger;

/**
 * @author Adam Wasiljew
 */
public class ShortRunningTask implements Task {

    private static final Logger log = Logger.getLogger(ShortRunningTask.class);

    @Override
    public void run() {
        log.info("ShortRunningTask DONE!");
    }


}
