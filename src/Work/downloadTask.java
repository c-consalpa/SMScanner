package Work;

import Utils.Common;
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
    private final String version;



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
            DProduct dProduct = new DProduct(product, version);
            int latestBuildNumber = dProduct.getRemoteBuildNumber();
            int currentBuildBumber = FSUtils.getCurrentBuildNumber(product, version);
            if (currentBuildBumber == latestBuildNumber) {
                controller.consoleLog("Current " + product + " " + version +
                                     "(" + currentBuildBumber + ")" + " is up-to-date; Skipping download..");
                continue;
            } else {
                FSUtils.cleanupFolders();
            }




        }
    }


//
    private void checkConnection() throws IOException {
        controller.consoleLog("Checking connection..");
        File f = new File(Common.BASE_PATH);
        if (!f.exists()) {
            throw new IOException("Cannot access the server");
        }
        controller.consoleLog("Connection OK");
    }
//
//    private void getDownloadParams() {
//        netBrowser netBrowser = new netBrowser(productName, version);
//        currentBuildNumber = Utils.FSUtils.getCurrentBuildNumber(productName, version);
//        latestBuildNumber = netBrowser.getLatestBuildNumber();
//        File latestBuildURL = netBrowser.getLatestBuildPath(latestBuildNumber);
//        if(latestBuildURL==null) {
//            controller.consoleLog("Cannot find matching files in the folder");
//            return;
//        }
//        String latestBuildName = latestBuildURL.getName();
//        targetFolderPath = Utils.FSUtils.getHomeFolder(productName, version);
//
//                downloadTo = new File(targetFolderPath, latestBuildName);
//                downloadFrom = latestBuildURL;
//
//        }
//
//    private boolean downloadData(File downloadFrom, File downloadTo) {
//        controller.consoleLog("INITIATING DOWNLOAD : " + downloadFrom);
//        try (
//                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(downloadFrom));
//                BufferedOutputStream bfOut = new BufferedOutputStream(new FileOutputStream(downloadTo))
//            ) {
//            int tmp;
//            while((tmp=bfIn.read())!=-1) {
//                if(isCancelled()) {
////TODO do cheaper cancelling
//                    bfOut.close();
//                    downloadTo.delete();
//                    FSUtils.persistLatestDownload(downloadTo.getParentFile(), productName, currentBuildNumber);
//                    controller.consoleLog("CANCELLING DOWNLOAD: " + downloadFrom);
//                    return false;
//                }
//                bfOut.write(tmp);
//            }
//            bfOut.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        controller.consoleLog("DOWNLOADED : "+downloadTo);
//        return true;
//    }
}
