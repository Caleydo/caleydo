package org.caleydo.core.application.helper.cacher.kegg;

import java.util.ArrayList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FileTransferClient;

public class KeggPathwayImageCacher
{
    private int iConcurrentFtpConnections = 0;
	
	public void run()
	{
		String sServerName = "ftp.genome.ad.jp";
		String sDirName = "/pub/kegg/pathway/organisms/hsa/";
	
        // set up logger so that we get some output
        Logger log = Logger.getLogger(KeggPathwayImageCacher.class);
        log.setLevel(Level.INFO);

        FileTransferClient ftp = null;

        try {
            // create client
            log.info("Creating FTP client");
            ftp = new FileTransferClient();
            ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);

            // set remote host
            ftp.setRemoteHost(sServerName);
            ftp.setUserName("anonymous");
            ftp.setPassword("");

            // connect to the server
            log.info("Connecting to server " + sServerName);
            ftp.connect();
            log.info("Connected and logged in to server " + sServerName);

//            ftp.changeDirectory(sDirName);
            
            log.info("Getting current directory listing");
            FTPFile[] files = ftp.directoryList(sDirName);
            ftp.disconnect();
            
            
            ArrayList<KeggSinglePathwayImageCacherThread> threadContainer = new ArrayList<KeggSinglePathwayImageCacherThread>();
            
            String sTmpFileName = "";
            int iPatternIndex = 0;
            
            for (int i = 0; i < files.length; i++) 
            {
            	if (iConcurrentFtpConnections > 30)
            	{
            		Thread.sleep(500);
            		i--;
            	}
            	
            	sTmpFileName = files[i].toString();
            	
                // Download only image files
                if (sTmpFileName.contains(".gif"))
                {
                	iPatternIndex = sTmpFileName.indexOf(".gif");
                	sTmpFileName = sTmpFileName.substring(iPatternIndex - 8, iPatternIndex + 4);
                   
                	threadContainer.add(new KeggSinglePathwayImageCacherThread(this, sTmpFileName, sDirName));
                	iConcurrentFtpConnections++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void threadFinishNotification() 
	{
		iConcurrentFtpConnections--;
	}
	
	public static void main(String[] args)
	{
		KeggPathwayImageCacher keggCacher = new KeggPathwayImageCacher();
		keggCacher.run();
	}
}
