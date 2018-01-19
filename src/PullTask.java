import FSUtils.FSUtil;
import netUtils.FilePuller;
import netUtils.netBrowser;


import java.io.File;
import java.io.FilenameFilter;
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
        String latestBuildName = netBrowser.getLatestBuildName();
        String currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, productVersion);
        File srcPath = netBrowser.getLatestBuildPath();
        File targetPath = FSUtil.getDownloadFolder(productName, productVersion, latestBuildName);

        System.out.println("build name: " +latestBuildName);
        System.out.println(latestBuildNumber);
        System.out.println(currentBuildNumber);
        System.out.println(srcPath);
        System.out.println(targetPath);

        if (Integer.parseInt(currentBuildNumber) >= Integer.parseInt(latestBuildNumber)) {
            System.out.println("Current " +productName+" is up-to-date");
            return;
        }
        FSUtil.cleanupFolder(targetPath);

        new FilePuller(srcPath, latestBuildName);

    }

}