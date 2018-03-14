package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Common {
    public static String HOMEFS_BUILDS_FOLDER = "D:\\\\Builds";
    public static String FILE_EXTENSION = "pdf";
    public static String PROPERTY_FILE_NAME = "latest.properties";
//    Properties that carry latest version info on local fs/net
    public static String PROPERTY_BUILD_NUMBER_KEY = "_b_build";
    public static String PROPERTY_BUILD_NUMBER_KEY_LOCAL = "b_version";




    public static int getBuildNumberFromProps(File file, String propertyName) {
        int buildNumber = 0;
        Properties props = new Properties();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            props.load(fileReader);
            String tmp = props.getProperty(propertyName);

            if(tmp==null) {
//                Quit if the PROPERTY_BUILD_NUMBER_KEY prop does not exist;
                return buildNumber;
            }

            if (!tmp.isEmpty()) {
                buildNumber = Integer.parseInt(tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader!=null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buildNumber;
    }
}
