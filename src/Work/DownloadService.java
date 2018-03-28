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
//// TODO: 3/21/2018 check if cancel() terminates service looping 
public class DownloadService extends ScheduledService<String> {
    private final String[] products;
    private final String version;
    private final File destination;
    MainAppController controller;

    public DownloadService(String[] products, String version, File destination, MainAppController mainAppController) {
        this.products = products;
        this.version = version;
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
        controller.consoleLog("DOWNLOAD CYCLE COMPLETE");
        controller.consoleLog("***********");
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
