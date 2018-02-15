package Work;

import FSUtils.FSUtil;
import javafx.concurrent.Task;
import netUtils.netBrowser;

import java.io.*;
import java.nio.file.Path;

public class downloadTask extends Task<String> {
    private String productName;
    private String productVersion;
    private final String[] products;
    private final String version;
    private final int pollingInterval;
    private final File destination;

    private File downloadFrom;
    private File downloadTo;

    public downloadTask(String[] products, String version, int pollingInterval, File destination) {
        this.products = products;
        this.version = version;
        this.pollingInterval = pollingInterval;
        this.destination = destination;
    }

    @Override
    protected String call() throws Exception {
        System.out.println(Thread.currentThread().getName());
        for (String product:
             products) {
            System.out.println("IT#");
            productName = product;
            productVersion = version;

            setBuildParams();
            downloadData(downloadFrom, downloadTo);
        }
        return "";
    }

    private void downloadData(File downloadFrom, File downloadTo) {
        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(downloadFrom));
                BufferedOutputStream bfOut = new BufferedOutputStream(
                        new FileOutputStream(downloadTo));
            ) {

            System.out.println("Downloading from: "+downloadFrom);
            int tmp;
            while((tmp=bfIn.read())!=-1) {
                bfOut.write(tmp);
            }
            bfOut.flush();
            System.out.println("File downloaded: ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBuildParams() {
        netBrowser netBrowser = new netBrowser(productName, productVersion);

        int latestBuildNumber = netBrowser.getLatestBuildNumber();
        String latestBuildName = netBrowser.getLatestBuildName();
        int currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, productVersion);
        File targetFolderPath = FSUtil.getTargetFolder(productName, productVersion);

        downloadTo = new File(targetFolderPath, latestBuildName);
        downloadFrom = netBrowser.getLatestBuildPath();

        if (currentBuildNumber >= latestBuildNumber) {
            System.out.println("Current " + productName + " " + productVersion +
                    "("+ currentBuildNumber + ")" + " is up-to-date");
            return;
        }
    }

}
