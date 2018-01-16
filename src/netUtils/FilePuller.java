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
        BufferedInputStream bfIn=null;
        BufferedOutputStream bfOut=null;
        try {
            bfIn = new BufferedInputStream(new FileInputStream(destination));
            bfOut = new BufferedOutputStream(new FileOutputStream(new File("D://test.pdf")));
            int tmp;
            while((tmp=bfIn.read())!=-1) {
                bfOut.write(tmp);
            }
            bfOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bfIn!=null){
                    bfIn.close();
                }
                if (bfOut!=null) {
                    bfOut.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
