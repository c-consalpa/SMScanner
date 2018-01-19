package FSUtils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.getenv;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class FSUtil {
    static String BUILDS_FOLDER_BASE = "D:\\\\Builds\\";


    public static void initHomeFolders(String prdctNm, String prdctVrsn) {
        File buildsFolder = new File("D:\\\\Builds\\" + prdctNm + "\\" + prdctVrsn);
        if (!buildsFolder.exists()) {
            buildsFolder.mkdirs();
            System.out.println("Folders initialized");
        }

    }

    public static String getCurrentBuildNumber(String prdctNm, String prdctVrsn) {
        String res = "";
        String ECRootPath = System.getenv("ECRootPath");
        String currentProductBuild = "";
        File currentProductDirectory = new File(ECRootPath +
                "\\" +
                prdctNm);
        File versionTxt = new File(currentProductDirectory, "version.txt");
        if (versionTxt.exists()) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(versionTxt));
                currentProductBuild = bufferedReader.readLine();
                res = getVersionInt(currentProductBuild);
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
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


    public static File getDownloadFolder(String productName, String productVersion, String buildName) {
        File srcFile = new File("D:\\\\Builds\\" + productName + "\\" + productVersion + "\\" + buildName);
        return srcFile;
    }


    public static void cleanupFolder(File target) {
        try {
                FileUtils.cleanDirectory(target.getParentFile());
                Thread.sleep(5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
}
