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
    int latestBuildNumber;
    File targetFolderPath;

    private File downloadFrom;
    private File downloadTo;

    public downloadTask(String[] products, String version, MainAppController controller) {
        this.products = products;
        this.version = version;
        this.controller = controller;
        Utils.FSUtils.initHomeFolders(products, version);
        controller.consoleLog("Starting task: " + String.valueOf(new Date()));
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
    protected String call() throws Exception {
        doStuff();
        return "";
    }

    private void doStuff() {

        for (String product :
                products) {
            productName = product;
// setBuildParams() gets info required for download and returns bool whether the download is needed;
            boolean isUpToDate = setBuildParams();
            if (isUpToDate) {
                continue;
            }
            FSUtils.cleanupFolders(targetFolderPath);
            downloadData(downloadFrom, downloadTo);
            FSUtils.persistLatestDownload(targetFolderPath, latestBuildNumber);
        }
    }

    private void checkConnection() {
        System.out.println("Checking connection");
        File f = new File(netBrowser.BASE_PATH);
        if (!f.exists()) {
            try {
                throw new Exception("Cannot access the server");
            } catch (Exception e) {

                controller.consoleLog("Cannot access the server. Terminating.");
                e.printStackTrace();
            }
        }
    }


    private boolean setBuildParams() {
        boolean isUpToDate = false;
//        checkConnection();
        netBrowser netBrowser = new netBrowser(productName, version);

        int currentBuildNumber = Utils.FSUtils.getCurrentBuildNumber(productName, version);
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
                System.out.println("Can't get downloadFrom link. Aborting");
                e.printStackTrace();
                isUpToDate = true;
            }
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
