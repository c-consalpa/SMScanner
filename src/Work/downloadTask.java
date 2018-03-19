package Work;

import Utils.FSUtils;
import GUI.MainAppController;
import javafx.concurrent.Task;
import Utils.netBrowser;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

public class downloadTask extends Task<String> {
    private MainAppController controller;

    private final String[] products;
    private String productName;
    private final String version;
    int latestBuildNumber,
        currentBuildNumber;
    File targetFolderPath;

    private File downloadFrom;
    private File downloadTo;

    public downloadTask(String[] products, String version, MainAppController controller) {
        this.products = products;
        this.version = version;
        this.controller = controller;
        Utils.FSUtils.initHomeFolders(products, version);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        System.out.println("TASK CANCELLED");
    }

    @Override
    protected void succeeded() {
        super.succeeded();
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println("TASK FAILED");
        System.out.println(Arrays.toString(getException().getStackTrace()));
    }

    @Override
    protected String call() {
        controller.consoleLog("STARTING TASK: " + String.valueOf(new Date()));
        makeCycle();
        return "";
    }

    private void makeCycle() {
        try {
            checkConnection();
        } catch (IOException e) {
            controller.consoleLog("Cannot access the server. Terminating cycle.");
            e.printStackTrace();
        }

        for (String product : products) {
            productName = product;
            // setBuildParams() gets info required for download and returns bool whether the download is needed;
            boolean isUpToDate = setBuildParams();
            if (isUpToDate) continue;
            FSUtils.cleanupFolders(targetFolderPath);
            Boolean downloadResults = downloadData(downloadFrom, downloadTo);
            if (downloadResults) FSUtils.persistLatestDownload(targetFolderPath, latestBuildNumber);
        }
    }

    private void checkConnection() throws IOException {
        controller.consoleLog("Checking connection..");
        File f = new File(netBrowser.BASE_PATH);
        if (!f.exists()) {
            throw new IOException("Cannot access the server");
        }
        controller.consoleLog("Connection OK");
    }

    private boolean setBuildParams() {
        boolean isUpToDate = false;

        netBrowser netBrowser = new netBrowser(productName, version);
        currentBuildNumber = Utils.FSUtils.getCurrentBuildNumber(productName, version);
        latestBuildNumber = netBrowser.getLatestBuildNumber();
        String latestBuildName = netBrowser.getLatestBuildName();
        targetFolderPath = Utils.FSUtils.getHomeFolder(productName, version);

        if (currentBuildNumber >= latestBuildNumber) {
            controller.consoleLog("Current " + productName + " " + version +
                    "("+ currentBuildNumber + ")" + " is up-to-date; Skipping download..");
            isUpToDate = true;
        } else {
            try {
                downloadTo = new File(targetFolderPath, latestBuildName);
                downloadFrom = netBrowser.getLatestBuildPath();
            } catch (FileNotFoundException e) {
                controller.consoleLog("Can't get downloadFrom link. Aborting");
                e.printStackTrace();
                isUpToDate = true;
            }
        }
        return isUpToDate;
    }

    private boolean downloadData(File downloadFrom, File downloadTo) {
        controller.consoleLog("INITIATING DOWNLOAD : " + downloadFrom);
        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(downloadFrom));
                BufferedOutputStream bfOut = new BufferedOutputStream(new FileOutputStream(downloadTo))
            ) {
            int tmp;
            while((tmp=bfIn.read())!=-1) {
                if(isCancelled()) {
                    bfOut.close();
                    downloadTo.delete();
                    FSUtils.persistLatestDownload(downloadTo.getParentFile(), currentBuildNumber);
                    controller.consoleLog("CANCELLING DOWNLOAD: " + downloadFrom);
                    return false;
                }
                bfOut.write(tmp);
            }
            bfOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.consoleLog("DOWNLOADED : "+downloadTo);
        return true;
    }
}
