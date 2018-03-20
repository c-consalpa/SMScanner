package Utils;

import java.io.*;
import java.util.Arrays;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class netBrowser {
    private File remote_productsFolder;
    public netBrowser(String productName, String version) {
        remote_productsFolder = new File(Common.BASE_PATH +
                FSUtils.FS_DELIMITER +
                version +
                FSUtils.FS_DELIMITER +
                productName);
    }

    public int getLatestBuildNumber() {
        int buildNumber = 0;
        buildNumber = getLatestBuildNumberByFolder(remote_productsFolder);
        return buildNumber;
    }

    private int getLatestBuildNumberByFolder(File remote_productsFolder) {
        String[] buildFolders = remote_productsFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        if (buildFolders.length<1) {
            return 0;
        }
        Arrays.sort(buildFolders);
        return Integer.parseInt(buildFolders[buildFolders.length-1]);
    }

    public File getLatestBuildPath(int latestBuildNumber) {
        File path = null;
        File buildFolder = new File(remote_productsFolder, Integer.toString(latestBuildNumber));

        if (!buildFolder.exists()) {
            try {
                throw new FileNotFoundException("Can't locate file. Build folder does not exists: "+ buildFolder);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //Attempting to find a file from extensions arr
        File[] files = buildFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (int i = 0; i < Common.FILE_EXTENSION.length; i++) {
                    if (name.endsWith(Common.FILE_EXTENSION[i])) {
                        return true;
                    }
                }
                return false;
            }
        });
        //Pick the first file from those that match the extension
        if (files.length>0) {
            for (String ext : Common.FILE_EXTENSION) {
                for (File f:
                     files) {
                    if (f.getName().endsWith(ext)) {
                        System.out.println("FILE FOUND: " + f);
                        path=f;
                        return path;
                    }
                }
            }
        } else {
            System.out.println("NO MATCHING FILES FOUND");
        }
        return path;
    }

    public String getLatestBuildName() {
        String latestBuildFileName = "DEFAULT";
//        try {
//            latestBuildFileName = getLatestBuildPath(latestBuildNumber).getName();
//        } catch (FileNotFoundException e) {
//            System.out.println("Cannot get artifact name; artifact path");
//            e.printStackTrace();
//        }
        return latestBuildFileName;
    }


}
