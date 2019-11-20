package Work;

import Utils.FSUtils;
import GUI.MainAppController;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.File;

public class DownloadService extends ScheduledService<String> {
    private final String[] products;
    private final String version;
    private final File destination;
    MainAppController controller;
    public DownloadTask currentTask = null;

    public DownloadService(String[] products, String version, File destination, MainAppController mainAppController) {
        this.products = products;
        this.version = version;
        this.destination = destination;
        controller = mainAppController;
    }

    @Override
    protected Task createTask() {
        currentTask = new DownloadTask(products, version, controller);
        return currentTask;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        System.out.println(getLastValue());
    }


    @Override
    protected void failed() {
        super.failed();
        System.out.println("Service failed. Reason: " + getException());
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        System.out.println("Service Cancelled");
    }
}
