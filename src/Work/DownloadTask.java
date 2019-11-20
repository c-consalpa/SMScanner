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
    //This field is queried by mainApp to get what is currently downloading;
    private String currentlyDownloadedBuild;
    private int errorsCount = 0;

    public DownloadTask(String[] products, String version, MainAppController controller) {
        this.products = products;
        this.version = version;
        this.controller = controller;
        Utils.FSUtils.initHomeFolders(products, version);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        controller.nullifyProgress();
        if (errorsCount > 0) {
            controller.consoleLog("Download session complete with errors: " + errorsCount);
        } else {
            controller.consoleLog("Download session complete");
        }
        controller.consoleLog("***********");
    }

    @Override
    protected void running() {
        super.running();
        controller.setOverallProgress(0d);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        System.out.println("Download session cancelled");
    }

    @Override
    protected void failed() {
        super.failed();
        Exception e = (Exception) getException();
        System.out.println(e.toString());
        System.out.println("Download session failed");
        System.out.println(Arrays.toString(e.getStackTrace()));
        controller.consoleLog("***********");
    }

    @Override
    protected String call() {
        controller.consoleLog("Starting download session: " + String.valueOf(new Date()));
        makeCycle();
        return "DownloadTask finished " + this;
    }

    private void makeCycle() {
        try {
            checkConnection();
        } catch (IOException e) {
            controller.consoleLog("The server is unavailable. Terminating cycle.");
            return;
        }

        for (int i = 0; i < products.length; i++) {
            String productName = products[i];
//            if a product download is cancelled, no need to waste time with others in products[]:
            if (isCancelled()) return;

            currentlyDownloadedBuild = productName;
            DProduct dProduct = new DProduct(productName, version, controller);
            try {
                dProduct.findProductFolder();
            } catch (FileNotFoundException e) {
                controller.consoleLog(e.getMessage());
                controller.setOverallProgress((i + 1) * ((double) 1 / products.length));
                errorsCount++;
                continue;
            }
            // if enbuild06/Builds/ProductName is found:

            int latestBuildNumber   = dProduct.getLatestBuildNumber();
            int currentBuildNumber  = dProduct.getCurrentBuildNumber(productName, version);
            if (currentBuildNumber == latestBuildNumber) {
                controller.consoleLog("Current " + productName
                                        + " " + version
                                        + "(" + currentBuildNumber + ") "
                                        + "is up-to-date; Skipping download..");
                controller.setOverallProgress((i + 1) * ((double) 1 / products.length));
                continue;
            } else {
                FSUtils.cleanupFolders(productName, version);
                File artifact_remote = null;
                try {
                    artifact_remote = dProduct.buildPath2Artifact_remote(latestBuildNumber);
                } catch (FileNotFoundException e) {
                    controller.consoleLog(e.getMessage());
                    controller.setOverallProgress((i + 1) * ((double) 1 / products.length));
                    errorsCount++;
                    continue;
                }
                String artifactName_remote = artifact_remote.getName();
                File artifact_local = dProduct.buildPath2Artifact_local(artifactName_remote);

                controller.setCurrentArtifactName(productName);
                boolean downloadSuccessful;
                downloadSuccessful = downloadFiles(artifact_remote, artifact_local);

                if (downloadSuccessful) {
                    dProduct.persistLatestDownload(artifact_local.getParentFile(), productName, latestBuildNumber);
                    Platform.runLater(() -> {
                        try {
                            MNotification mNotification = new MNotification(productName, String.valueOf(latestBuildNumber), artifact_local.getParentFile());
                            mNotification.show();
                        } catch (IOException e) {
                            System.out.println("Can't compose notification stage;");
                            e.printStackTrace();
                        }
                    });
                }
            }

            if (!isCancelled()) {
                controller.setOverallProgress((i + 1) * ((double) 1 / products.length));
            } else {
                    controller.nullifyProgress();
            }
        }
    }

    private boolean downloadFiles(File pathToArtifact_remote, File pathToArtifact_local) {
        //TODO rewrite this awful 165-year-old-slow-granddad-like downloader
        controller.consoleLog("Initiating download: " + pathToArtifact_remote);
        controller.setIndividualProgress(0d);
//        check destination dirs for changes made within previous cycle:
        if (!pathToArtifact_local.getParentFile().exists()) {
            pathToArtifact_local.mkdirs();
        }
        // vars to calulate time elapsed per 1 download
        long startTime;
        long endTime;
        //      fields used to update the progress of download process:
        final long FILE_SIZE = pathToArtifact_remote.length();
        final double PROGRESS_PRECISION = 100;
        final double progressTickBytes = FILE_SIZE / PROGRESS_PRECISION;
        double threshold = progressTickBytes;
        long currentByte = 0;
        double progressUpdateFireCount = 1;

        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(pathToArtifact_remote));
                BufferedOutputStream bfOut = new BufferedOutputStream(new FileOutputStream(pathToArtifact_local))
        ) {
//            int enough for files up to 2.1 GB each
            int tmpByte;
            startTime = System.currentTimeMillis();
            while ((tmpByte = bfIn.read()) != -1) {
                currentByte++;
                if (isCancelled()) {
                    bfOut.close();
                    pathToArtifact_local.delete();
                    controller.consoleLog("Task cancelled. Removing file: " + pathToArtifact_local);
                    return false;
                }
        //  check if another 1/PRECISION portion of file is downloaded:
                if(currentByte >= threshold) {
                    controller.setIndividualProgress(++progressUpdateFireCount * ((double) 1 / PROGRESS_PRECISION));
                    threshold += progressTickBytes;
                }
                bfOut.write(tmpByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        endTime = System.currentTimeMillis();
        controller.consoleLog("File Downloaded: " + pathToArtifact_local);
        controller.consoleLog(getElapsedTime(endTime - startTime));
        return true;
    }

    private String getElapsedTime(long elapsed) {
        StringBuffer sb = new StringBuffer();
        sb.append("Elapsed time: ");

        int remain = (int) (elapsed / 1000);
        int secs = 0;
        int mins = 0;
        int hrs = 0;

        // calc hours
        if (remain / 3600 > 0) {
            hrs = remain / 3600;
            remain = remain - hrs * 3600;
            sb.append(hrs);
            sb.append(" hour");
            if (hrs > 1) sb.append("s");
            sb.append(" ");
        }
        // calc minutes
        if (remain / 60 > 0) {
            mins = remain / 60;
            remain = remain - mins * 60;
        }
        sb.append(mins + " minute");
        if (mins > 1) sb.append("s");
        sb.append(" ");

        // the rest are seconds
        secs = remain;
        sb.append(secs + " seconds");

        return sb.toString();
    }

    private void checkConnection() throws IOException {
        controller.consoleLog("Checking connection..");
        File f = new File(Common.BASE_PATH);
        if (!f.exists()) {
            throw new IOException("Cannot access the server");
        }
        controller.consoleLog("Connection OK");
    }

    public String getCurrentBuildName() {
        return (currentlyDownloadedBuild == null)?"none":currentlyDownloadedBuild;
    }
}
