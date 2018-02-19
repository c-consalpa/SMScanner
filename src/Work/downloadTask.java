package Work;

import Utils.FSUtils;
import GUI.MainAppController;
import javafx.concurrent.Task;
import Utils.netBrowser;

import java.io.*;
import java.util.Date;

public class downloadTask extends Task<String> {
    private MainAppController controller;

    private final String[] products;
    private String productName;
    private final String version;
    int latestBuildNumber;
    File targetFolderPath;

    private File downloadFrom;
    private File downloadTo;

    public downloadTask(String[] products, String version, MainAppController controller) {
        this.products = products;
        this.version = version;
        this.controller = controller;
        Utils.FSUtils.initHomeFolders(products, version);
        controller.consoleLog(String.valueOf(new Date()));
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        controller.consoleLog(getValue());
        controller.consoleLog("*********************************");
    }

    @Override
    protected void failed() {
        super.failed();

    }

    @Override
    protected String call() throws Exception {
        doStuff();
        return "Task complete";
    }

    private void doStuff() {
        for (String product: products) {
            productName = product;

            boolean isUpToDate = setBuildParams();
            if (!isUpToDate) {
                FSUtils.cleanupFolder(targetFolderPath);
                downloadData(downloadFrom, downloadTo);
                FSUtils.persistLatestDownload(targetFolderPath, latestBuildNumber);
            }
        }
    }


    private boolean setBuildParams() {
        boolean isUpToDate = false;
        netBrowser netBrowser = new netBrowser(productName, version);

        int currentBuildNumber = Utils.FSUtils.getCurrentBuildNumber(productName, version);
        latestBuildNumber = netBrowser.getLatestBuildNumber();
        String latestBuildName = netBrowser.getLatestBuildName();
        targetFolderPath = Utils.FSUtils.getTargetFolder(productName, version);

        if (currentBuildNumber >= latestBuildNumber) {
            controller.consoleLog("Current " + productName + " " + version +
                    "("+ currentBuildNumber + ")" + " is up-to-date; Skipping download..");
            isUpToDate = true;
        } else {
            downloadTo = new File(targetFolderPath, latestBuildName);
            downloadFrom = netBrowser.getLatestBuildPath();
        }
        return isUpToDate;
    }

    private void downloadData(File downloadFrom, File downloadTo) {
        controller.consoleLog("INITIATING DOWNLOAD : "+downloadFrom);
        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(downloadFrom));
                BufferedOutputStream bfOut = new BufferedOutputStream(
                        new FileOutputStream(downloadTo))
            ) {
            int tmp;
            while((tmp=bfIn.read())!=-1) {
                bfOut.write(tmp);
            }
            bfOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.consoleLog("DOWNLOADED : "+downloadTo);
    }
}
