package netUtils;

import GUI.MainApp;
import GUI.MainAppController;
import org.apache.commons.io.IOUtils;

import java.io.*;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class FilePuller {
<<<<<<< HEAD:src/netUtils/FilePuller.java
    FileWriter fileWriter;
    public FilePuller(File src, File trgt, String latestBuildName, MainAppController controller) {
        System.out.println("FilePuller:"+Thread.currentThread().getName());
=======
    public FilePuller(File src, File trgt, String latestBuildName, MainAppController controller) {
>>>>>>> 2f76e3b4fe55b3c7c1fadc4f4914b9d74dbe8401:src/netUtils/FilePuller.java
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
<<<<<<< HEAD:src/netUtils/FilePuller.java


=======
>>>>>>> 2f76e3b4fe55b3c7c1fadc4f4914b9d74dbe8401:src/netUtils/FilePuller.java
}
