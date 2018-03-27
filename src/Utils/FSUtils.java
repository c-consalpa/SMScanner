package Utils;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Utils.Common;

import javax.rmi.CORBA.Util;

import static Utils.Common.*;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class FSUtils {

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
                    String comments = String.format(Common.propsFileTemplate, productName);
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

    public static void cleanupFolders(String product, String version) {
        File homeBuildsFolder = new File(Common.HOMEFS_BUILDS_FOLDER);
        if (!homeBuildsFolder.exists()) return;

        File[] files2Clean = homeBuildsFolder.listFiles((dir, name) -> {
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

}
