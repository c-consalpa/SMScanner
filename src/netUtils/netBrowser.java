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
        File propsFile = new File(remote_productsFolder, "latest.properties");
        Properties props = new Properties();
        Reader propsInReader;
        try {
            propsInReader = new BufferedReader(new FileReader(propsFile));
            props.load(propsInReader);
            propsInReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can't find .properties file at: "+ remote_productsFolder);
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            System.out.println("Error while reading .property file.");
            e.printStackTrace();
            return 1;
        }
        String buildNumber = props.getProperty("_b_build");
        return Integer.parseInt(buildNumber);
    }

    public String getLatestBuildName() {
        return getLatestBuildPath().getName();
    }


}
