package Work;

import GUI.MainAppController;
import Utils.Common;
import com.sun.javafx.collections.MappingChange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static Utils.Common.FS_DELIMITER;
import static Utils.Common.HOMEFS_BUILDS_FOLDER;
import static Utils.Common.PROPERTY_BUILD_NUMBER_KEY_LOCAL;

/**
 * Created by c-consalpa on 3/23/2018.
 */
public class DProduct {
    private File productsRemoteFolder;
    private File productsHomeFolder;
    private MainAppController controller;
    private String productName;

    public DProduct(String product, String version, MainAppController controller) {
        productsRemoteFolder = new File(Common.BASE_PATH +
                    Common.FS_DELIMITER +
                    version +
                    Common.FS_DELIMITER +
                    product);

        productsHomeFolder = new File(Common.HOMEFS_BUILDS_FOLDER +
                FS_DELIMITER +
                version +
                FS_DELIMITER +
                product +
                FS_DELIMITER);
        this.controller = controller;
        this.productName = product;
    }

    public int getRemoteBuildNumber() {
        int biggestnum = 0;
        if (productsRemoteFolder ==null) {
            System.out.println("Cannot get builds list;");
            return biggestnum;
        }
        String[] buildnumberFolders = productsRemoteFolder.list();
        for (String buildnumberFolder:
             buildnumberFolders) {
            if (buildnumberFolder.matches("\\d+") && (Integer.parseInt(buildnumberFolder) > biggestnum)) {
                biggestnum = Integer.parseInt(buildnumberFolder);
            }
        }
        return biggestnum;
    }

    public int getCurrentBuildNumber(String prdctNm, String prdctVrsn) {
        int buildNumber = 0;
        File propsDstn = new File(HOMEFS_BUILDS_FOLDER +
                FS_DELIMITER +
                prdctVrsn +
                FS_DELIMITER +
                prdctNm +
                FS_DELIMITER +
                Utils.Common.PROPERTY_FILE_NAME);
        if (propsDstn.exists()) {
        //           If file not existing - buildNumber=0;
            buildNumber = Common.getBuildNumberFromProps(propsDstn, PROPERTY_BUILD_NUMBER_KEY_LOCAL);
        }
        return buildNumber;
    }

    public File getDownloadFromURL(int buildNumber) {
        File remoteFolder = new File(productsRemoteFolder, String.valueOf(buildNumber));
        if (!remoteFolder.exists()) {
            System.out.println("Can't locate remote build folder.");
            return null;
        }
        File from = null;
        try {
            from = pickSuitableFile(remoteFolder);
        } catch (FileNotFoundException e) {
            controller.consoleLog(e.getMessage());
            controller.consoleLog("Skipping product: " + productName);
            return null;
        }
        return from;
    }

    private File pickSuitableFile(File buildNumberFolder) throws FileNotFoundException {
    //searches folder if there a file with ending with string from Common.EXTENSIONS
        File[] artifactsInFolder = buildNumberFolder.listFiles();
        for (int i = 0; i < Common.FILE_EXTENSIONS.length; i++) {
            for (int j = 0; j < artifactsInFolder.length; j++) {
                if (artifactsInFolder[j].getName().endsWith(Common.FILE_EXTENSIONS[i])) {
                    System.out.println("File found: " + artifactsInFolder[j].getName());
                    return artifactsInFolder[j];
                }
            }

        }
        throw new FileNotFoundException("Can't find matching files in folder: " + buildNumberFolder.getAbsolutePath());
    }

    public File getToURL(String productFileName) {
        if (!productsHomeFolder.exists()) {
            productsHomeFolder.mkdir();
        }
        return new File(productsHomeFolder, productFileName);
    }

    public void persistLatestDownload(File targetFolderPath, String productName, int latestBuildNumber) {
        Properties props = new Properties();
        try (
                FileOutputStream propsOutputStream = new FileOutputStream(
                        new File(targetFolderPath, Common.PROPERTY_FILE_NAME));
                ){
            String comments = String.format(Common.propsFileTemplate, productName);
            props.setProperty(PROPERTY_BUILD_NUMBER_KEY_LOCAL, String.valueOf(latestBuildNumber));
            props.store(propsOutputStream, comments);
        } catch (FileNotFoundException e) {
            System.out.println("CANT PERSIST LATEST BUILD NUMBER. CANT FIND PROPERTY FILE AT "+targetFolderPath);
        } catch (IOException e) {
            System.out.println("CANT WRITE TO PROPERTY FILE AT " + targetFolderPath);
        }
    }
}
