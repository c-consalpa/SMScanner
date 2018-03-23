package Work;

import Utils.Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by c-consalpa on 3/23/2018.
 */
public class DProduct {
    private File ProductsRemoteFolder ;

    public DProduct(String product, String version) {
            ProductsRemoteFolder = new File(Common.BASE_PATH +
                    Common.FS_DELIMITER +
                    version +
                    Common.FS_DELIMITER +
                    product);
    }

    public int getRemoteBuildNumber() {
        if (ProductsRemoteFolder==null) {
            System.out.println("Cannot get builds list;");
            return 0;
        }
        File[] buildnumberFolders = ProductsRemoteFolder.listFiles((dir, name) -> {
            if (name.matches("\\d+")) return true;
            return false;
        });
        if(buildnumberFolders.length==0) {
            System.out.println("Cannot get remote build version");
            return 0;
        } else {
            Arrays.sort(buildnumberFolders);
            String latestBuildFolderName = buildnumberFolders[buildnumberFolders.length-1].getName();
            return Integer.parseInt(latestBuildFolderName);
        }
    }


}
