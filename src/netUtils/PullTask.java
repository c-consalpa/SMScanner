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
    private String[] productNames;
    private String productVersion;
    public PullTask(String[] products, String version, MainAppController mainAppController) {
        productNames = products;
        productVersion = version;
        this.controller = mainAppController;
    }

    @Override
    public void run() {
        controller.getConsoleTextArea().appendText(new Date().toString());
        for (String productName: productNames) {
            downloadProduct(productName);
        }
        controller.consoleLog("*********");
    }

    private void downloadProduct(String productName) {
        netBrowser netBrowser = new netBrowser(productName, productVersion);

        int latestBuildNumber = netBrowser.getLatestBuildNumber();
        String latestBuildName = netBrowser.getLatestBuildName();
        File srcFilePath = netBrowser.getLatestBuildPath();

        int currentBuildNumber = FSUtil.getCurrentBuildNumber(productName, productVersion);
        File targetFolderPath = FSUtil.getTargetFolder(productName, productVersion);

        controller.consoleLog("Latest build name: "    + latestBuildName);
        controller.consoleLog("Latest build number: "  + latestBuildNumber);
        controller.consoleLog("Current build number: " + currentBuildNumber);
        controller.consoleLog("Source Path: "          + srcFilePath);
        controller.consoleLog("Target folder: "          + targetFolderPath);

        if (currentBuildNumber >= latestBuildNumber) {
            controller.consoleLog("Current " + productName + " " + productVersion +
                    "("+ currentBuildNumber + ")" + " is up-to-date");
            return;
        }

        FSUtil.cleanupFolder(targetFolderPath);
        new FilePuller(srcFilePath, targetFolderPath, latestBuildName, controller);
        FSUtil.updateLatestTxt(targetFolderPath, latestBuildNumber);
    }
}