package netUtils;

import FSUtils.FSUtil;
import GUI.MainApp;
import GUI.MainAppController;
import javafx.scene.control.TextArea;


import java.io.File;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class PullTask extends TimerTask {
    public MainAppController controller;
    public TextArea console;
    private String[] productNames;
    private String productVersion;
    public PullTask(String[] products, String version, MainAppController mainAppController) {
        productNames = products;
        productVersion = version;
        this.controller = mainAppController;
        console = controller.getConsoleTextArea();
    }

    @Override
    public void run() {
        controller.getConsoleTextArea().appendText(new Date().toString());
        for (String productName: productNames) {
            downloadProduct(productName);
        }
        System.out.println("*********");
    }

    private void downloadProduct(String productName) {
        netBrowser netBrowser = new netBrowser(productName, productVersion);
        int latestBuildNumber = netBrowser.getLatestBuildNumber();
        String latestBuildName = netBrowser.getLatestBuildName();
        int currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, productVersion);
        File srcFilePath = netBrowser.getLatestBuildPath();
        File targetFolderPath = FSUtil.getTargetFolder(productName, productVersion);

        System.out.println("Latest build name: "    + latestBuildName);
        System.out.println("Latest build number: "  + latestBuildNumber);
        System.out.println("Current build number: " + currentBuildNumber);
        System.out.println("Source Path: "          + srcFilePath);
        System.out.println("Target folder: "          + targetFolderPath);

        if (currentBuildNumber >= latestBuildNumber) {
            console.appendText("Current " + productName + " " + productVersion +
                    "("+ currentBuildNumber + ")" + " is up-to-date\r\n");
            return;
        }

        FSUtil.cleanupFolder(targetFolderPath);
//        new FilePuller(srcFilePath, targetFolderPath, latestBuildName);
        FSUtil.updateLatestTxt(targetFolderPath, latestBuildNumber);
    }
}