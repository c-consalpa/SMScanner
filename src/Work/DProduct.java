package Work;

import Utils.Common;

import java.io.File;

import static Utils.Common.FS_DELIMITER;

/**
 * Created by c-consalpa on 3/23/2018.
 */
public class DProduct {
    private File productsRemoteFolder;
    private File productsHomeFolder;

    public DProduct(String product, String version) {
        productsRemoteFolder = new File(Common.BASE_PATH +
                    Common.FS_DELIMITER +
                    version +
                    Common.FS_DELIMITER +
                    product);

        productsHomeFolder = new File(Common.HOMEFS_BUILDS_FOLDER +
                FS_DELIMITER +
                version +
                FS_DELIMITER +
                product +
                FS_DELIMITER);
    }

    public int getRemoteBuildNumber() {
        int biggestnum = 0;
        if (productsRemoteFolder ==null) {
            System.out.println("Cannot get builds list;");
            return biggestnum;
        }
        String[] buildnumberFolders = productsRemoteFolder.list();
        for (String buildnumberFolder:
             buildnumberFolders) {
            if (buildnumberFolder.matches("\\d+") && (Integer.parseInt(buildnumberFolder) > biggestnum)) {
                biggestnum = Integer.parseInt(buildnumberFolder);
            }
        }
        return biggestnum;
    }


    public File getFromURL(int buildNumber) {
        File remoteFolder = new File(productsRemoteFolder, String.valueOf(buildNumber));
        if (!remoteFolder.exists()) {
            System.out.println("Can't locate remote build folder.");
            return null;
        }
        File from = pickSuitableFile(remoteFolder.listFiles());
        return from;
    }

    private File pickSuitableFile(File[] folder) {
//searches folder if there a file with ending with string from Common.EXTENSIONS
        for (int i = 0; i < folder.length; i++) {
            for (int j = 0; j < Common.FILE_EXTENSION.length; j++) {
                if (folder[i].toString().endsWith(Common.FILE_EXTENSION[j])){
                    System.out.println("File found: "+folder[i]);
                    return new File(String.valueOf(folder[i]));
                }
            }
        }
        System.out.println("No matching files found");
        return null;
    }

    public File getToURL(String productFileName) {
        if (!productsHomeFolder.exists()) {
            productsHomeFolder.mkdir();
        }
        return new File(productsHomeFolder, productFileName);
    }
}
