import FSUtils.FSUtil;
import netUtils.*;

import java.util.TimerTask;

/**
 * Created by Konstantin on 14.01.2018.
 */
public class Main {
    public static void main(String[] args) {
        String product = "EAM";
        String version = "9.0.0";
        FSUtil.initHomeFolders(product, version);

        Scheduler scheduler = new Scheduler(product, version, 1000*60*60*24);


    }
}
