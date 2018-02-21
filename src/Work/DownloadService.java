package Work;

import Utils.FSUtils;
import GUI.MainAppController;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.File;

/**
 * Created by c-consalpa on 2/15/2018.
 */
public class DownloadService extends ScheduledService<String> {
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
    }

    @Override
    protected Task createTask() {
        return new downloadTask(products, version, controller);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        controller.consoleLog("task complete");
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println("Service failed");


    }
}
