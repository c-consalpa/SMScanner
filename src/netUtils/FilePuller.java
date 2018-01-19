package netUtils;

import org.apache.commons.io.IOUtils;

import java.io.*;


/**
 * Created by Konstantin on 14.01.2018.
 */
public class FilePuller {
    public FilePuller(File src, String name) {
        try (
                BufferedInputStream bfIn = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream bfOut = new BufferedOutputStream(new FileOutputStream(new File("D:\\Builds\\EAM\\9.0.0\\"+name)));
                ){

            System.out.println("Downloading from: "+src);

            int tmp;
            while((tmp=bfIn.read())!=-1) {
                bfOut.write(tmp);
            }
            bfOut.flush();
            System.out.println("File downloaded;");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
