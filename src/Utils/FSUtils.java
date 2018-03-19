package Utils;

import java.io.*;
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

    public static void initHomeFolders(String[] products, String prdctVrsn) {
        //TODO do normal .props intialization
        for (String productName:
             products) {
            File buildsFolder = new File(HOMEFS_BUILDS_FOLDER +
                    FS_DELIMITER +
                    prdctVrsn +
                    FS_DELIMITER +
                    productName);
            if (!buildsFolder.exists()) {
                buildsFolder.mkdirs();
                try {
                    File propsFile = new File(buildsFolder, PROPERTY_FILE_NAME);
                    propsFile.createNewFile();
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


    public static String getVersionInt(String line) {
        String result = "";
        Pattern pattern = Pattern.compile("\\d{3,}$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
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
        File[] files2Clean = targetFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(Utils.Common.FILE_EXTENSION)?true:false;
            }
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

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(targetFolderPath, PROPERTY_FILE_NAME));
            props.setProperty(Common.PROPERTY_BUILD_NUMBER_KEY_LOCAL, String.valueOf(latestBuildNumber));
            props.store(fileWriter, "Latest downloaded build number");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot update properties file");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter!=null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
