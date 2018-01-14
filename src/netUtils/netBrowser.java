package netUtils;

import java.io.*;
import java.util.Properties;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class netBrowser {
    private String productName;
    private String version;
    public static String BASE_PATH = "\\\\enbuild06\\Builds\\";
    File productDestination;

    public netBrowser(String productName, String version) {
        this.productName = productName;
        this.version = version;

        productDestination = new File(BASE_PATH +
                version +
                "\\" +
                productName);
        System.out.println("Connected to: "+productDestination.toString());
    }

    public String getLatestBuildNumber() {
        File propsFile = new File(productDestination, "latest.properties");
        Properties props = new Properties();
        Reader propsInReader=null;

        try {
            propsInReader = new BufferedReader(new FileReader(propsFile));
            props.load(propsInReader);
            propsInReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Error while reading .property file.");
            e.printStackTrace();
        }

        String buildNumber = props.getProperty("_b_build");
        System.out.println("Latest "+productName+ " build: " +buildNumber);
        return buildNumber;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("GC!");
    }
}
