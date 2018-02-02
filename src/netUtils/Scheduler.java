package netUtils;

import netUtils.PullTask;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class Scheduler {
    TimerTask task;
    public Scheduler(String[] products, String version, long interval) {
        task = new PullTask(products, version);
        new Timer().schedule(task, 1000, interval);
    }
}
