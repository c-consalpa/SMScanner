import FSUtils.FSUtil;
import netUtils.*;

import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class Main {
    public static void main(String[] args) {
        String[] products = {"EAM"};
        String version = "9.0.0";
        FSUtil.HOMEFS_BUILDS_FOLDER = "D:\\\\Builds\\";
        FSUtil.FILE_EXTENSION = "msi";

        FSUtil.initHomeFolders(products, version);
        new Scheduler(products, version, 100000);


    }
}
