package netUtils;

import org.apache.commons.io.IOUtils;

import java.io.*;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class FilePuller {
    File destination = null;
    public FilePuller(File dstntn) {
        System.out.println(dstntn);
        this.destination = dstntn;
        BufferedInputStream bfIn;
        BufferedOutputStream bfOut;

//        try {
////            bfIn = new BufferedInputStream(new FileInputStream(destination));
////            bfOut = new BufferedOutputStream(new FileOutputStream())
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
