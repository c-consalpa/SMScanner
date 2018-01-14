package netUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;



/**
 * Created by Konstantin on 14.01.2018.
 */
public class FilePuller {
    private String productName;
    private String buildNumber;
    public FilePuller(String product, String latestBuildNumber) {
        this.productName = product;
        this.buildNumber = latestBuildNumber;

//        BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream())
    }
}
