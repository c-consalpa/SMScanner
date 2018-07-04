package Work;

import GUI.MNotification;
import Utils.Common;
import Utils.FSUtils;
import GUI.MainAppController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.io.*;
import java.util.Date;


public class DownloadTask extends Task<String> {
    private MainAppController controller;
    private final String[] products;
    private final String version;
    //This field is queried by mainApp to get what is currently downloading;
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
        System.out.println("TASK SUCCESS");
        controller.setMaxProgressAndWait(products.length);
    }

    @Override
    protected void running() {
        super.running();
        controller.setOverallProgress(0);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        System.out.println("TASK CANCELLED");

    }

    @Override
    protected void failed() {
        super.failed();
        Exception e = (Exception) getException();
        System.out.println(e.toString());
        System.out.println("TASK FAILED");
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
            controller.consoleLog("The server is unavailable. Terminating cycle.");
            return;
        }

        for (int i = 0; i < products.length; i++) {
            String product = products[i];
//            if a product download is cancelled, no need to waste time with others in products[]:
            if (isCancelled()) continue;
            currentlyDownloadedBuild = product;
            DProduct dProduct = new DProduct(product, version, controller);
            int latestBuildNumber = dProduct.getLatestBuildNumber();
            int currentBuildNumber = dProduct.getCurrentBuildNumber(product, version);
//            if (currentBuildNumber == latestBuildNumber) {
//                controller.consoleLog("Current " + product + " " + version +
//                                     "(" + currentBuildNumber + ")" + " is up-to-date; Skipping download..");
//                continue;
//            } else {
                FSUtils.cleanupFolders(product, version);
                File remoteFile = dProduct.getDownloadFromURL(latestBuildNumber);
                if (remoteFile == null) {
//                    continue to next product if install file cannot be found
                    continue;
                }
                String productFileName = remoteFile.getName();
                File localFile = dProduct.getToURL(productFileName);
                boolean downloadSuccessfull = false;
                downloadSuccessfull = downloadFiles(remoteFile, localFile);
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
//            }

            if (i==0) {
                controller.bindOverallProgress(this);
            }
            updateProgress(i+1, products.length );
        }
    }

    private boolean downloadFiles(File from, File to) {
        //TODO rewrite this awful 165-year-old-slow-granddad-like downloader
        controller.consoleLog("INITIATING DOWNLOAD : " + from);
        if (!to.getParentFile().exists()) {
            to.mkdirs();
        }
        // vars to calulate time elapsed per 1 download
        long startTime = 0;
        long endTime = 0;

        //      fields used to update the progress of download process:
        final long FILE_SIZE = from.length();
        final double PROGRESS_PRECISION = 100;
        final double progressGraduationUnit = FILE_SIZE / PROGRESS_PRECISION;
        double threshold = progressGraduationUnit;
        long currentByte = 0;
        double progressUpdateFireCount = 1; // 2 because
        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(from));
                BufferedOutputStream bfOut = new BufferedOutputStream(new FileOutputStream(to))
        ) {
            int tmpByte;
            startTime = System.currentTimeMillis();
            while ((tmpByte = bfIn.read()) != -1) {
                currentByte++;
                if (isCancelled()) {
                    bfOut.close();
                    to.delete();
                    controller.consoleLog("Task cancelled. Removing file: " + to);
                    return false;
                }
        //  check if another 1/PRECISION portion of file is downloaded:
                if(currentByte >= threshold) {
                    controller.updateSingleProductProgress(++progressUpdateFireCount * ((double) 1 / PROGRESS_PRECISION));
                    threshold += progressGraduationUnit;
                }
                bfOut.write(tmpByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        endTime = System.currentTimeMillis();
        controller.consoleLog("FILE DOWNLOADED: " + to);
        controller.consoleLog(getElapsedTime(endTime - startTime));

        return true;
    }

    private String getElapsedTime(long elapsed) {
        StringBuffer sb = new StringBuffer();
        sb.append("Elapsed time: ");
        if (elapsed/1000/60/60 >= 1) sb.append((int) elapsed/1000/60/60 + " hours ");
        if (elapsed/1000/60 >= 1) sb.append((int) elapsed/1000/60 + " minutes ");
        if (elapsed/1000 >= 1) sb.append((int) elapsed/1000%60 + " seconds ");

        return (sb.toString());
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
