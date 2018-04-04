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
    public downloadTask currentTask = null;

    public DownloadService(String[] products, String version, File destination, MainAppController mainAppController) {
        this.products = products;
        this.version = version;
        this.destination = destination;

        controller = mainAppController;
    }

    @Override
    protected Task createTask() {
        currentTask = new downloadTask(products, version, controller);
        return currentTask;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        controller.consoleLog("DOWNLOAD CYCLE COMPLETE");
        controller.consoleLog("***********");
        System.out.println(getLastValue());
    }


    @Override
    protected void failed() {
        super.failed();
        System.out.println("SERVICE FAILED. REASON: "+getException());
    }

    @Override
    protected void cancelled() {
        super.cancelled();

        System.out.println("SERVICE CANCELLED");
    }
}
