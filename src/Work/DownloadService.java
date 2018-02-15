package Work;

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

    public DownloadService(String[] products, String version, int pollingInterval, File destination) {
        this.products = products;
        this.version = version;
        this.pollingInterval = pollingInterval;
        this.destination = destination;
    }

    @Override
    protected Task createTask() {
        return new downloadTask(products, version, pollingInterval, destination);
    }
}
