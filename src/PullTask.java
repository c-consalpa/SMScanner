import FSUtils.FSUtil;
import netUtils.FilePuller;
import netUtils.netBrowser;

import java.io.File;
import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class PullTask extends TimerTask {
    private String productName;
    private String productVersion;
    public PullTask(String product, String version) {
        productName = product;
        productVersion = version;
    }

    @Override
    public void run() {
        netBrowser netBrowser = new netBrowser(productName, productVersion);
        String latestBuildNumber = netBrowser.getLatestBuildNumber();

        File destination = new File(netBrowser.BASE_PATH +
                                    productVersion  +
                                    "\\"            +
                                    productName     +
                                    "\\"            +
                                    latestBuildNumber);
        System.out.println("DEstionation: " + destination);

        //new FilePuller(productName, latestBuildNumber);
    }
}
