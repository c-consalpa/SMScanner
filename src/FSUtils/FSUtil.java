package FSUtils;

import java.io.File;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class FSUtil {
    public static void cleanupBuildsFolder(String productName) {

    }

    public static void initHomeFolders(String product, String version) {
        File buildsFolder = new File("D:\\Builds");
        System.out.println(buildsFolder.exists());
    }
}
