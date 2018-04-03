package Work;

import Utils.Common;
import Utils.FSUtils;
import GUI.MainAppController;
import javafx.concurrent.Task;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

public class downloadTask extends Task<String> {
    private MainAppController controller;
    private final String[] products;
    private final String version;

    public downloadTask(String[] products, String version, MainAppController controller) {
        this.products = products;
        this.version = version;
        this.controller = controller;
        Utils.FSUtils.initHomeFolders(products, version);
    }

    @Override
    protected void succeeded() {
        super.succeeded();

    }

    @Override
    protected void cancelled() {
        super.cancelled();
        System.out.println("TASK CANCELLED");
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println("TASK FAILED");
        System.out.println(Arrays.toString(getException().getStackTrace()));
        controller.consoleLog("***********");
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
        }
        for (String product : products) {
            DProduct dProduct = new DProduct(product, version);
            int latestBuildNumber = dProduct.getRemoteBuildNumber();
            int currentBuildNumber = dProduct.getCurrentBuildNumber(product, version);
            if (currentBuildNumber == latestBuildNumber) {
                controller.consoleLog("Current " + product + " " + version +
                                     "(" + currentBuildNumber + ")" + " is up-to-date; Skipping download..");
                continue;
            } else {
                FSUtils.cleanupFolders(product, version);
                File remoteFile = dProduct.getDownloadFromURL(latestBuildNumber);
                String productFileName = remoteFile.getName();
                File localFile = dProduct.getToURL(productFileName);

                boolean downloadOK = downloadFiles(remoteFile, localFile);
                if (downloadOK) {
                    dProduct.persistLatestDownload(localFile.getParentFile(), product, latestBuildNumber);
                }
            }
        }
    }

    private boolean downloadFiles(File from, File to) {
        controller.consoleLog("INITIATING DOWNLOAD : " + from);
        if (!to.getParentFile().exists()) {
            to.mkdirs();
        }
        long startTime = 0;
        long endTime = 0;
        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(from));
                BufferedOutputStream bfOut = new BufferedOutputStream(new FileOutputStream(to))
        ) {
            int tmp;
            startTime = System.currentTimeMillis();
            while ((tmp = bfIn.read()) != -1) {
                if (isCancelled()) {
                    controller.consoleLog("Task cancelled. Removing file: " + to);
                    bfOut.close();
                    to.delete();
                    return false;
                }
                bfOut.write(tmp);
            }
            bfOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        endTime = System.currentTimeMillis();
        controller.consoleLog("Downloaded file: "+to);
        printElapsedTime(endTime-startTime);
        return true;
    }

    private void printElapsedTime(long elapsed) {
        StringBuffer sb = new StringBuffer();
        sb.append("Elapsed time: ");
        if (elapsed/1000/60/60 >= 1) sb.append((int) elapsed/1000/60/60 + " hours ");
        if (elapsed/1000/60 >= 1) sb.append((int) elapsed/1000/60 + " minutes ");
        if (elapsed/1000 >= 1) sb.append((int) elapsed/1000 + " seconds ");

        controller.consoleLog(sb.toString());

    }

    private void checkConnection() throws IOException {
        controller.consoleLog("Checking connection..");
        File f = new File(Common.BASE_PATH);
        if (!f.exists()) {
            throw new IOException("Cannot access the server");
        }
        controller.consoleLog("Connection OK");
    }

}
