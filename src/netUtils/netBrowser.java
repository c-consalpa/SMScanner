package netUtils;

import FSUtils.FSUtil;

import java.io.*;
import java.util.Properties;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class netBrowser {
    private String productName;
    private String version;
    public static String BASE_PATH = "\\\\enbuild06\\Builds\\";
    File productFolderDestination;


    public netBrowser(String productName, String version) {
        this.productName = productName;
        this.version = version;
        productFolderDestination = new File(BASE_PATH +
                version +
                "\\" +
                productName);
    }

    public File getLatestBuildPath() {
        File path = null;
        File buildFolder = new File(productFolderDestination, Integer.toString(getLatestBuildNumber()));
        System.out.println(buildFolder);
        File[] files = buildFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(FSUtil.FILE_EXTENSION)) {
                    return true;
                } else return false;
            }
        });
        if (files.length>0) {
            path = files[0];
        }
        return path;
    }

    public int getLatestBuildNumber() {
        File propsFile = new File(productFolderDestination, "latest.properties");
        Properties props = new Properties();
        Reader propsInReader=null;
        try {
            propsInReader = new BufferedReader(new FileReader(propsFile));
            props.load(propsInReader);
            propsInReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot connect to: "+productFolderDestination);
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            System.out.println("ERROR: Error while reading .property file.");
            e.printStackTrace();
        }
        String buildNumber = props.getProperty("_b_build");
        return Integer.parseInt(buildNumber);
    }

    public String getLatestBuildName() {
        return getLatestBuildPath().getName();
    }


}
