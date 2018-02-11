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
<<<<<<< HEAD
        new Timer().schedule(task, 1000, interval);

=======
        new Timer().schedule(task, 10000, interval);
        FSUtils.FSUtil.initHomeFolders(products, version);
>>>>>>> 2f76e3b4fe55b3c7c1fadc4f4914b9d74dbe8401
    }
}
