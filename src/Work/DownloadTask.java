package Work;

import GUI.MNotification;
import Utils.Common;
import Utils.FSUtils;
import GUI.MainAppController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.io.*;
import java.util.Arrays;
import java.util.Date;


public class DownloadTask extends Task<String> {
    private MainAppController controller;
    private final String[] products;
    private final String version;
    //This field is queries by mainApp to get what is currently downloading;
    private String currentlyDownloadedBuild;

    public DownloadTask(String[] products, String version, MainAppController controller) {
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
            return;
        }

        for (String product : products) {
//            if a product download is cancelled, no need to waste time with others in products[]:
            if (isCancelled()) continue;
            currentlyDownloadedBuild = product;
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
                boolean downloadSuccessfull = downloadFiles(remoteFile, localFile);
                if (downloadSuccessfull) {
                    dProduct.persistLatestDownload(localFile.getParentFile(), product, latestBuildNumber);
                    Platform.runLater(() -> {
                        try {
                            MNotification mNotification = new MNotification(product, String.valueOf(latestBuildNumber), localFile.getParentFile());
                            mNotification.show();
                        } catch (IOException e) {
                            System.out.println("Can't componse notification stage;");
                            e.printStackTrace();
                        }
                    });
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
//                    bfOut.flush();
                    bfOut.close();
                    to.delete();
                    controller.consoleLog("Task cancelled. Removing file: " + to);
                    return false;
                }
                bfOut.write(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        endTime = System.currentTimeMillis();
        controller.consoleLog("Downloaded file: " + to);
        printElapsedTime(endTime - startTime);
        return true;
    }


    private void printElapsedTime(long elapsed) {
        StringBuffer sb = new StringBuffer();
        sb.append("Elapsed time: ");
        if (elapsed/1000/60/60 >= 1) sb.append((int) elapsed/1000/60/60 + " hours ");
        if (elapsed/1000/60 >= 1) sb.append((int) elapsed/1000/60 + " minutes ");
        if (elapsed/1000 >= 1) sb.append((int) elapsed/1000%60 + " seconds ");

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

    public String getCurrentlyDownloadedBuild() {
        return currentlyDownloadedBuild;
    }
}
