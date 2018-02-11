package netUtils;

import GUI.MainApp;
import GUI.MainAppController;
import org.apache.commons.io.IOUtils;

import java.io.*;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class FilePuller {
    FileWriter fileWriter;
    public FilePuller(File src, File trgt, String latestBuildName, MainAppController controller) {
        System.out.println("FilePuller:"+Thread.currentThread().getName());
        File srcBuildPath = new File(trgt, latestBuildName);
        try (
                    BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(src));
                    BufferedOutputStream bfOut = new BufferedOutputStream(
                            new FileOutputStream(srcBuildPath));
                ){
            controller.consoleLog("Downloading from: "+src);
            int tmp;
            while((tmp=bfIn.read())!=-1) {
                bfOut.write(tmp);
            }
            bfOut.flush();
            controller.consoleLog("File downloaded: " + srcBuildPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
