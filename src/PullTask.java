import FSUtils.FSUtil;
import netUtils.FilePuller;
import netUtils.netBrowser;


import java.io.File;
import java.io.FilenameFilter;
import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class PullTask extends TimerTask {
    private String productName;
    private String productVersion;
    public PullTask(String product, String version) {
        productName = product;
        productVersion = version;
    }

    @Override
    public void run() {
        netBrowser netBrowser = new netBrowser(productName, productVersion);
        int latestBuildNumber = Integer.parseInt(netBrowser.getLatestBuildNumber());
//        int currentBuildNumber = Integer.parseInt(FSUtil.getCurrentBuildNumber(productName, productVersion));
//        if (currentBuildNumber >= latestBuildNumber) {
//            System.out.println("Current " +productName+" is up-to-date");
//            return;
//        }

        File destinationFolder = new File(netBrowser.BASE_PATH +
                                    productVersion  +
                                    "\\"            +
                                    productName     +
                                    "\\"            +
                                    latestBuildNumber);

     new FilePuller(FileToDownload(destinationFolder));
    }

    File FileToDownload(File dstntnFldr) {
        File link = new File("");
       File[] files = dstntnFldr.listFiles(new FilenameFilter() {
            @Override
        public boolean accept(File dir, String name) {
           if(name.endsWith("exe")) {
               return true;
           } else return false;
        }
    });
        if (files.length == 1) {
            link=files[0];
        }
        System.out.println("assembled link: "+link);
        return link;
    }
}