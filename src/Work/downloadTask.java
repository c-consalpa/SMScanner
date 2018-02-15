package Work;

import FSUtils.FSUtil;
import GUI.MainAppController;
import javafx.concurrent.Task;
import netUtils.netBrowser;

import java.io.*;

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
    }

    @Override
    protected void succeeded() {
        super.succeeded();


    }

    @Override
    protected String call() throws Exception {
        doStuff();

        return "qsd";
    }

    private void doStuff() {
        for (String product: products) {
            productName = product;

            boolean isUpToDate = setBuildParams();
            if (!isUpToDate) {
                cleanupFolder(targetFolderPath);
                downloadData(downloadFrom, downloadTo);
                updateCurrentBuildNumber(targetFolderPath, latestBuildNumber);
            }
        }
    }


    private boolean setBuildParams() {
        boolean isUpToDate = false;
        netBrowser netBrowser = new netBrowser(productName, version);

        String latestBuildName = netBrowser.getLatestBuildName();
        latestBuildNumber = netBrowser.getLatestBuildNumber();
        int currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, version);
        targetFolderPath = FSUtil.getTargetFolder(productName, version);

        if (currentBuildNumber >= latestBuildNumber) {
            System.out.println("Current " + productName + " " + version +
                    "("+ currentBuildNumber + ")" + " is up-to-date; Skipping download..");
            isUpToDate = true;
        } else {
            downloadTo = new File(targetFolderPath, latestBuildName);
            downloadFrom = netBrowser.getLatestBuildPath();
        }
        return isUpToDate;
    }

    private void downloadData(File downloadFrom, File downloadTo) {
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
        System.out.println("download finished: "+downloadTo);

    }

    private void updateCurrentBuildNumber(File targetFolderPath, int latestBuildNumber) {
        File latestTxt = new File(targetFolderPath, "latest.txt");
        try (
                FileWriter fileWriter = new FileWriter(latestTxt);
        ) {
            fileWriter.write(Integer.toString(latestBuildNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void cleanupFolder(File targetFolder) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File[] files2Clean = targetFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FSUtil.FILE_EXTENSION)?true:false;
            }
        });

        for (File f :
                files2Clean) {
            if (!f.isDirectory()) {
                f.delete();
            }
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
