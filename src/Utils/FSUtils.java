package Utils;

import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by Konstantin on 14.01.2018.
 */
public class FSUtils {
    public static String HOMEFS_BUILDS_FOLDER = "D:\\\\Builds\\";
    public static String FILE_EXTENSION = "pdf";
    public static String PROPERTY_FILE_NAME = "latest.properties";
    public static String PROPERTY_BUILD_NUMBER_KEY = "b_version";

    public static void initHomeFolders(String[] products, String prdctVrsn) {
        for (String productName:
             products) {
            File buildsFolder = new File(HOMEFS_BUILDS_FOLDER +
                    prdctVrsn +
                    "\\" +
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
        File dstn = new File(HOMEFS_BUILDS_FOLDER +
                            "\\" +
                            prdctVrsn +
                            "\\" +
                            prdctNm +
                            "\\" +
                            "latest.properties");
        getBuildNumberFromProps(dstn, PROPERTY_BUILD_NUMBER_KEY);
        System.out.println(buildNumber);
        return buildNumber;
    }

    public static String getBuildNumberFromProps(File file, String propertyName) {
        System.out.println(file);
        String buildNumber = "";
        Properties props = new Properties();
        try {
            FileReader fileReader = new FileReader(file);
            props.load(fileReader);
            String tmp = props.getProperty(PROPERTY_BUILD_NUMBER_KEY);
            if (!tmp.isEmpty()) {
                buildNumber = tmp;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    public static File getTargetFolder(String productName, String productVersion) {
        File targetFolder = new File(HOMEFS_BUILDS_FOLDER +
                productVersion +
                "\\" +
                productName +
                "\\");

        return targetFolder.isDirectory()?targetFolder:null;
    }

    public static void cleanupFolder(File targetFolder) {
        File[] files2Clean = targetFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(Utils.FSUtils.FILE_EXTENSION)?true:false;
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
            props.setProperty(PROPERTY_BUILD_NUMBER_KEY, String.valueOf(latestBuildNumber));
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
