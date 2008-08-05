package org.caleydo.core.application.helper.cacher.kegg;

import java.io.IOException;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;

/**
 * Loads all pathways of a certain type by providing the XML path.
 * 
 * @author Marc Streit
 */
public class KeggSinglePathwayImageCacherThread
	extends Thread
{	
	private String sFileName;

	private KeggPathwayImageCacher keggCacher;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param sXMLPath
	 */
	public KeggSinglePathwayImageCacherThread(KeggPathwayImageCacher keggCacher,
			String sFileName,
			String sDirName)
	{
		this.sFileName = sFileName;
		this.keggCacher = keggCacher;
		
		start();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		super.run();
		
		try
		{
			String sServerName = "ftp.genome.ad.jp";
			String sDirName = "/pub/kegg/pathway/organisms/hsa/";
			
            FileTransferClient ftp = new FileTransferClient();
            ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);

            ftp.setRemoteHost(sServerName);
            ftp.setUserName("anonymous");
            ftp.setPassword("");
            ftp.connect();
			
//          System.out.println("Start downloading file " +sFileName);
            
			ftp.downloadFile(System.getProperty("user.home") 
					+ "/.caleydo/kegg/" + sFileName, sDirName + sFileName);
		
//			System.out.println("Finished downloading file " +sFileName);
			
            ftp.disconnect();
            
            keggCacher.threadFinishNotification();
		}
		catch (FTPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
