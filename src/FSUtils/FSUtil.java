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
    public static String HOMEFS_BUILDS_FOLDER;
    public static String FILE_EXTENSION;




    public static void initHomeFolders(String[] products, String prdctVrsn) {
        for (String productName:
             products) {
            File buildsFolder = new File(HOMEFS_BUILDS_FOLDER +
                    prdctVrsn +
                    "\\" +
                    productName);
            if (!buildsFolder.exists()) {
                buildsFolder.mkdirs();
                System.out.println("Folders initialized");
            }
        }
    }

    public static int getCurrentBuildNumber(String prdctNm, String prdctVrsn) {
        int version = 0;
        File latestTxt = new File(HOMEFS_BUILDS_FOLDER +
                prdctVrsn +
                "\\" +
                prdctNm +
                "\\" +
                "latest.txt");

        if (!latestTxt.exists()) {
            return version=1;
        } else {
            try (
                    BufferedReader bReader = new BufferedReader(new FileReader(latestTxt));
            ) {
                String tmp = bReader.readLine();
                if(tmp!=null && !tmp.isEmpty()) {
                   version=Integer.parseInt(tmp);
                } else {
                    version = 1;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return version;
    }

    public static String getCurrentBuildNumberFromInstalledProduct(String prdctNm, String prdctVrsn) {
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

    public static File getTargetFolder(String productName, String productVersion) {
        File targetFolder = new File(HOMEFS_BUILDS_FOLDER +
                productVersion +
                "\\" +
                productName +
                "\\");

        return targetFolder.isDirectory()?targetFolder:null;
    }

    public static void cleanupFolder(File targetFolder) {
            File[] files2Clean = targetFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(FILE_EXTENSION)?true:false;
                }
            });

        for (File f :
                files2Clean) {
            if (!f.isDirectory()) {
                f.delete();
            }
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void updateLatestTxt(File targetFolderPath, int latestBuildNumber) {
        File latestTxt = new File(targetFolderPath, "latest.txt");
       try (
               FileWriter fileWriter = new FileWriter(latestTxt);
               ) {
           fileWriter.write(Integer.toString(latestBuildNumber));
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

}
