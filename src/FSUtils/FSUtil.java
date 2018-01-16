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
                getVersionInt(currentProductBuild);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (prdctNm) {
            case "EAM" : {
                res = System.getenv("EAMRoot");
            }
            case "XEServer" : {
                res = System.getenv("XESRoot");
            }
        }
        return res;
    }

    public static String getVersionInt(String line) {
        String result="";
        String v = "8.8.1";
        Pattern pattern = Pattern.compile("\\d{3,}$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("found");
            System.out.println(matcher.group());
        }
        return result;
    }
}
