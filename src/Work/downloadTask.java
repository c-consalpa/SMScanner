package Work;

import FSUtils.FSUtil;
import javafx.concurrent.Task;
import netUtils.netBrowser;

import java.io.*;
import java.nio.file.Path;

public class downloadTask extends Task<String> {
    private String productName;
    private String productVersion;
    private Path downloadToPath;
    private int latest;
    private File downloadFrom;
    private File downloadTo;

    public downloadTask(String productName, String productVersion) {
        this.productName = productName;
        this.productVersion = productVersion;
    }

    @Override
    protected String call() throws Exception {
        getBuildParams();
        downloadData(downloadFrom, downloadTo);
        return "returnString";
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

    private void getBuildParams() {
        netBrowser netBrowser = new netBrowser(productName, productVersion);

        int latestBuildNumber = netBrowser.getLatestBuildNumber();
        String latestBuildName = netBrowser.getLatestBuildName();
        int currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, productVersion);
        downloadFrom = netBrowser.getLatestBuildPath();
        File targetFolderPath = FSUtil.getTargetFolder(productName, productVersion);
        downloadTo = new File(targetFolderPath, latestBuildName);


        if (currentBuildNumber >= latestBuildNumber) {
            System.out.println("Current " + productName + " " + productVersion +
                    "("+ currentBuildNumber + ")" + " is up-to-date");
            return;
        }


    }

}
