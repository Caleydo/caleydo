package org.caleydo.core.application.helper.cacher.kegg;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FileTransferClient;

/**
 * Fetch tool for KEGG image files.
 * 
 * @author Marc Streit
 *
 */
public class KeggPathwayImageCacher
	extends Thread
{
	private static final int EXPECTED_DOWNLOADS = 214;
	
	/**
	 * Needed for async access to set progress bar state
	 */
	private Display display;
	
	private ProgressBar progressBar;
	
    private int iConcurrentFtpConnections = 0;	
	
	int iDownloadCount = 0;
	
	/**
	 * Constructor.
	 */
	public KeggPathwayImageCacher(final Display display, final ProgressBar progressBar)
	{
		this.display = display;
		this.progressBar = progressBar;
	}
	
	/*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
	public void run()
	{
		String sServerName = "ftp.genome.ad.jp";
		String sDirName = "/pub/kegg/pathway/organisms/hsa/";
	
        // set up logger so that we get some output
        Logger log = Logger.getLogger(KeggPathwayImageCacher.class);
        log.setLevel(Level.INFO);
        
        // Create KEGG folder in .caleydo
        new File(System.getProperty("user.home") + "/.caleydo/kegg").mkdir();

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
            
            final int iFilesToDownload = files.length;
            
            for (int iFileCount = 0; iFileCount < iFilesToDownload; iFileCount++) 
            {
            	if (iConcurrentFtpConnections > 30)
            	{
            		Thread.sleep(500);
            		iFileCount--;
            		continue;
            	}
            	
            	sTmpFileName = files[iFileCount].toString();
            	
                // Download only image files
                if (sTmpFileName.contains(".gif"))
                {
                	iPatternIndex = sTmpFileName.indexOf(".gif");
                	sTmpFileName = sTmpFileName.substring(iPatternIndex - 8, iPatternIndex + 4);
                   
                	threadContainer.add(new KeggSinglePathwayImageCacherThread(this, sTmpFileName, sDirName));
                	iConcurrentFtpConnections++;
                	
                	iDownloadCount++;
                	
					display.asyncExec(new Runnable()
					{
						public void run()
						{
							if (progressBar.isDisposed())
								return;
							progressBar.setSelection((int)(iDownloadCount * 100 / EXPECTED_DOWNLOADS));
						}
					});
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
	
	/**
	 * Main method for testing.
	 */
	public static void main(String[] pArgs) throws Exception
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		final ProgressBar progressBar = new ProgressBar(shell, SWT.SMOOTH);
		progressBar.setBounds(10, 10, 200, 32);
		shell.open();
		
		KeggPathwayImageCacher keggPathwayImageCacher = new KeggPathwayImageCacher(display, progressBar);
		keggPathwayImageCacher.start();
		
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
