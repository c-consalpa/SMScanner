package Utils;

import Utils.FSUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class netBrowser {
    private String productName;
    private String version;
    public static String BASE_PATH = "\\\\enbuild06\\Builds\\";
    File remote_productsFolder;

    public netBrowser(String productName, String version) {
        this.productName = productName;
        this.version = version;
        remote_productsFolder = new File(BASE_PATH +
                version +
                "\\" +
                productName);
    }

    public int getLatestBuildNumber() {
        int buildNumber = 0;
        File propsFile = new File(remote_productsFolder, "latest.properties");
        if (!propsFile.exists()) {
//            If .properties file cannot be found
           buildNumber =  getLatestBuildNumberByFolder(remote_productsFolder);
        } else {
            buildNumber = Utils.Common.getBuildNumberFromProps(propsFile, Common.PROPERTY_BUILD_NUMBER_KEY);
        }
        return buildNumber;
    }

    private int getLatestBuildNumberByFolder(File remote_productsFolder) {
        File remoteProductDirectory = remote_productsFolder;
        String[] buildFolders = remote_productsFolder.list();
        if (buildFolders.length<1) {
            return 1;
        }
        Arrays.sort(buildFolders);
        return Integer.parseInt(buildFolders[buildFolders.length-1]);
    }

    public File getLatestBuildPath() throws FileNotFoundException {
        File path = null;
        File buildFolder = new File(remote_productsFolder, Integer.toString(getLatestBuildNumber()));

        if (!buildFolder.exists()) {
            throw new FileNotFoundException("Can't locate file. Build folder does not exists: "+ buildFolder);
        }
        File[] files = buildFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(Utils.Common.FILE_EXTENSION)) {
                    return true;
                } else return false;
            }
        });
        if (files.length>0) {
            path = files[0];
        } else {
            System.out.println("Cant get "+ Common.FILE_EXTENSION + " in "+" "+buildFolder);
            throw new FileNotFoundException("Can't locate file");
        }
        return path;
    }

    public String getLatestBuildName() {
        String latestBuildFileName = "DEFAULT";
        try {
            latestBuildFileName = getLatestBuildPath().getName();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot get artifact name; artifact path");
            e.printStackTrace();
        }
        return latestBuildFileName;
    }


}
