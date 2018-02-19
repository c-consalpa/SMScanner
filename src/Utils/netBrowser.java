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

    public File getLatestBuildPath() {
        File path = null;
        File buildFolder = new File(remote_productsFolder, Integer.toString(getLatestBuildNumber()));
        File[] files = buildFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(Utils.FSUtils.FILE_EXTENSION)) {
                    return true;
                } else return false;
            }
        });
        if (files.length>0) {
            path = files[0];
        } else {
            try {
                throw new FileNotFoundException("Can't get latest build path.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public int getLatestBuildNumber() {
       int buildNumber = 0;
        File propsFile = new File(remote_productsFolder, "latest.properties");
        String tmp = Utils.FSUtils.getBuildNumberFromProps(propsFile, "_b_build");
       if (!tmp.isEmpty()) {
           buildNumber = Integer.parseInt(tmp);
       }
        return buildNumber;
    }

    private int getLatestBuildNumberByFolder(File remote_productsFolder) {
        File remoteProductDirectory = remote_productsFolder;
        String[] buildFolders = remote_productsFolder.list();
        if (buildFolders.length==0) {
            return 1;
        }
        Arrays.sort(buildFolders);
        return Integer.parseInt(buildFolders[buildFolders.length-1]);
    }

    public String getLatestBuildName() {
        String latestBuildFileName = "";
        latestBuildFileName = getLatestBuildPath().getName();
        return latestBuildFileName;
    }


}
