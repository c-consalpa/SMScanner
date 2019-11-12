package Work;

import GUI.MainAppController;
import Utils.Common;

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
    private final String productName;
    private final String productVersion;
    private final MainAppController controller;

    private File remoteArtifactsFolder;
    private File productsHomeFolder;


    DProduct(String productName, String productVersion, MainAppController controller) {
        this.productName = productName;
        this.productVersion = productVersion;
        this.controller = controller;

        productsHomeFolder = new File(Common.HOMEFS_BUILDS_FOLDER +
                FS_DELIMITER +
                productVersion +
                FS_DELIMITER +
                productName +
                FS_DELIMITER);
    }

    void findProductFolder() throws FileNotFoundException {
        remoteArtifactsFolder = new File(Common.BASE_PATH +
                Common.FS_DELIMITER +
                productVersion +
                Common.FS_DELIMITER +
                productName);
        if(!remoteArtifactsFolder.exists()) {
            throw new FileNotFoundException("Cannot locate directory: " + remoteArtifactsFolder.getAbsolutePath());
        }
    }

    int getLatestBuildNumber() {
        int biggestnum = 0;

        String[] buildsFoldersArr = remoteArtifactsFolder.list();
        for (String buildnumberFolder : buildsFoldersArr) {
            if (buildnumberFolder.matches("\\d+") && (Integer.parseInt(buildnumberFolder) > biggestnum)) {
                biggestnum = Integer.parseInt(buildnumberFolder);
            }
        }
        return biggestnum;
    }

    int getCurrentBuildNumber(String productName, String productVersion) {
        int buildNumber = 0;
        File propsDstn = new File(HOMEFS_BUILDS_FOLDER +
                FS_DELIMITER    +
                productVersion  +
                FS_DELIMITER    +
                productName     +
                FS_DELIMITER    +
                Utils.Common.PROPERTY_FILE_NAME);
        if (propsDstn.exists()) {
        //           If file does not exits -> buildNumber = 0;
            buildNumber = Common.getBuildNumberFromProps(propsDstn, PROPERTY_BUILD_NUMBER_KEY_LOCAL);
        }
        return buildNumber;
    }

    File buildPath2Artifact_remote(int buildNumber) throws FileNotFoundException {
        File remoteFolder = new File(remoteArtifactsFolder, String.valueOf(buildNumber));
        if (!remoteFolder.exists()) {
            throw new FileNotFoundException("Folder does not exist: " + remoteFolder);
        }
            File artifact = pickSuitableFile(remoteFolder);
            return artifact;
    }

    private File pickSuitableFile(File buildNumberFolder) throws FileNotFoundException {
    //scans remote folder if there a file one of Common.EXTENSIONS
        File[] artifactsInFolder = buildNumberFolder.listFiles();
        for (int i = 0; i < Common.FILE_EXTENSIONS.length; i++) {
            for (int j = 0; j < artifactsInFolder.length; j++) {
//                Search for file with name: ${ProductName} ...  .Common.EXTENSONS[i];
//                eg.: "SpecBuilder_ASDASDASD123123123123.exe"
                if(artifactsInFolder[j].getName().matches(productName + ".*\\." + Common.FILE_EXTENSIONS[i])) {
                    System.out.println(artifactsInFolder[j].getName());
                    return artifactsInFolder[j];
                }
            }
        }
        throw new FileNotFoundException("Can't find matching files in folder: " + buildNumberFolder.getAbsolutePath());
    }

    File buildPath2Artifact_local(String productFileName) {
        if (!productsHomeFolder.exists()) {
            productsHomeFolder.mkdir();
        }
        return new File(productsHomeFolder, productFileName);
    }

    void persistLatestDownload(File targetFolderPath, String productName, int latestBuildNumber) {
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
