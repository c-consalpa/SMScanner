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
    private String[] productNames;
    private String productVersion;
    public PullTask(String[] products, String version) {
        productNames = products;
        productVersion = version;
    }

    @Override
    public void run() {
        for (String productName: productNames) {

            netBrowser netBrowser = new netBrowser(productName, productVersion);
            int latestBuildNumber = netBrowser.getLatestBuildNumber();
            String latestBuildName = netBrowser.getLatestBuildName();
            int currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, productVersion);
            File srcFilePath = netBrowser.getLatestBuildPath();
            File targetFolderPath = FSUtil.getTargetFolder(productName, productVersion);

            System.out.println("Latest build name: "    + latestBuildName);
            System.out.println("Latest build number: "  + latestBuildNumber);
            System.out.println("Current build number: " + currentBuildNumber);
            System.out.println("Source Path: "          + srcFilePath);
            System.out.println("Target folder: "          + targetFolderPath);

            if (currentBuildNumber >= latestBuildNumber) {
                System.out.println("Current " +productName+" is up-to-date");
                return;
            }

            FSUtil.cleanupFolder(targetFolderPath);
            new FilePuller(srcFilePath, targetFolderPath, latestBuildName);
            FSUtil.updateLatestTxt(targetFolderPath, latestBuildNumber);
        }
    }
}