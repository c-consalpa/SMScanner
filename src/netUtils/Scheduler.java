package netUtils;

import GUI.MainAppController;
import netUtils.PullTask;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class Scheduler {
    TimerTask task;

    public Scheduler(String[] products, String version, long interval, MainAppController mainAppController) {
        task = new PullTask(products, version, mainAppController);
        new Timer().schedule(task, 10000, interval);
        FSUtils.FSUtil.initHomeFolders(products, version);
    }
}
