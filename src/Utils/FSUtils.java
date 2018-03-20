package Utils;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Utils.Common;

import static Utils.Common.*;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class FSUtils {
    public static String FS_DELIMITER = "\\";

    public static void initHomeFolders(String[] products, String productVersion) {
        for (String productName:
             products) {
            File buildsFolder = new File(HOMEFS_BUILDS_FOLDER +
                    FS_DELIMITER +
                    productVersion +
                    FS_DELIMITER +
                    productName);
            if (!buildsFolder.exists()) {
                buildsFolder.mkdirs();

                    Properties props = new Properties();
                    props.setProperty("b_version", "0");
                    String comments = "Dummy build number";
                    File propsFile = new File(buildsFolder, PROPERTY_FILE_NAME);
                try {
                    FileOutputStream propsOutStream = new FileOutputStream(propsFile);
                    props.store(propsOutStream, comments);
                    propsOutStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Folders initialized: " + buildsFolder);
            }
        }
    }

    public static int getCurrentBuildNumber(String prdctNm, String prdctVrsn) {
        int buildNumber = 0;
        File propsDstn = new File(HOMEFS_BUILDS_FOLDER +
                            FS_DELIMITER +
                            prdctVrsn +
                            FS_DELIMITER +
                            prdctNm +
                            FS_DELIMITER +
                            Common.PROPERTY_FILE_NAME);
        if (propsDstn.exists()) {
//           If file not existing - buildNumber=0;
            buildNumber = Common.getBuildNumberFromProps(propsDstn, Common.PROPERTY_BUILD_NUMBER_KEY_LOCAL);
        }

        return buildNumber;
    }

    public static File getHomeFolder(String productName, String productVersion) {
        File HomeProductFolder = new File(Common.HOMEFS_BUILDS_FOLDER +
                FS_DELIMITER +
                productVersion +
                FS_DELIMITER +
                productName +
                FS_DELIMITER);
    if (!HomeProductFolder.exists()) {
//        Create new folder if it was deleted
        HomeProductFolder.mkdir();
    }
        return HomeProductFolder;
    }

    public static void cleanupFolders(File targetFolder) {
        File[] files2Clean = targetFolder.listFiles((dir, name) -> {
            for (int i = 0; i < Common.FILE_EXTENSION.length; i++) {
                if(name.endsWith(Common.FILE_EXTENSION[i])) {
                    return true;
                }
            }
            return false;
        });
        for (File f :
                files2Clean) {
            if (!f.isDirectory()) {
                f.delete();
            }
        }
    }

    public static void persistLatestDownload(File targetFolderPath, int latestBuildNumber) {
        Properties props = new Properties();
        try {
            FileOutputStream propsOutputStream = new FileOutputStream(
                    new File(targetFolderPath, Common.PROPERTY_FILE_NAME));
            props.setProperty(Common.PROPERTY_BUILD_NUMBER_KEY_LOCAL, String.valueOf(latestBuildNumber));
            props.store(propsOutputStream, "Latest downloaded "+targetFolderPath.toString()+" build;");
        } catch (FileNotFoundException e) {
            System.out.println("CANT PERSIST LATEST BUILD NUMBER. CANT FIND PROPERTY FILE AT "+targetFolderPath);
        } catch (IOException e) {
            System.out.println("CANT WRITE TO PROPERTY FILE AT "+targetFolderPath);
        }
    }



}
