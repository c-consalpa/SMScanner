package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Common {
    public static String HOMEFS_BUILDS_FOLDER = "D:\\\\Builds";
    public static final String[] FILE_EXTENSIONS = {"zip", "msi", "exe"};
    public static final String PROPERTY_FILE_NAME = "latest.properties";
    public static final String BASE_PATH = "\\\\enbuild06\\Builds";
//    public static final String BASE_PATH = "D:\\emulated\\Builds";
    public static final String FS_DELIMITER = "\\";
    //    Properties that carry latest version info on local fs/net
    public static final String PROPERTY_BUILD_NUMBER_KEY_LOCAL = "b_version";
    public static final String propsFileTemplate = "Last downloaded %s build;";


    public static int getBuildNumberFromProps(File propertyFile, String propertyName) {
        int buildNumber = 0;
        Properties props = new Properties();
        try (
                FileReader fileReader = new FileReader(propertyFile)
                ) {
            props.load(fileReader);
            String tmp = props.getProperty(propertyName);
            if(tmp == null) {
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
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return buildNumber;
    }


}
