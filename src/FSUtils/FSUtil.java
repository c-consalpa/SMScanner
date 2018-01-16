package FSUtils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.getenv;

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
        String result="";
        Pattern pattern = Pattern.compile("\\d{3,}$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }
}
