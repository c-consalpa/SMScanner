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

    MainAppController controller;
    public DownloadTask currentTask = null;

    public DownloadService(String[] products, String version, MainAppController mainAppController) {
        this.products = products;
        this.version = version;
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
