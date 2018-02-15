package Work;

import FSUtils.FSUtil;
import GUI.MainAppController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;

/**
 * Created by c-consalpa on 2/15/2018.
 */
public class DownloadService extends Service {
    private final String[] products;
    private final String version;
    private final int pollingInterval;
    private final File destination;
    MainAppController controller;

    public DownloadService(String[] products, String version, int pollingInterval, File destination, MainAppController mainAppController) {
        this.products = products;
        this.version = version;
        this.pollingInterval = pollingInterval;
        this.destination = destination;
        controller = mainAppController;
        FSUtil.initHomeFolders(products, version);
    }

    @Override
    protected Task createTask() {
        return new downloadTask(products, version, controller);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        controller.consoleLog(Thread.currentThread().getName());
    }
}
