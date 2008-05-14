package org.caleydo.core.manager.data.pathway;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Loads all pathways of a certain type by providing the XML path.
 * 
 * @author Marc Streit
 *
 */
public class PathwayLoaderThread 
extends Thread 
{
	private IGeneralManager generalManager;
	
	private Collection<PathwayDatabase> pathwayDatabases;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param sXMLPath
	 */
	public PathwayLoaderThread(final IGeneralManager generalManager,
			final Collection<PathwayDatabase> pathwayDatabases) 
	{
		super("Pathway Loader Thread"); 
		
		this.generalManager = generalManager;
		this.pathwayDatabases = pathwayDatabases;
		
		start();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() 
	{
		Iterator<PathwayDatabase> iterPathwayDatabase = pathwayDatabases.iterator();
		
		while (iterPathwayDatabase.hasNext())
		{
			loadAllPathwaysByType(iterPathwayDatabase.next().getXMLPath());
		}
		return;
	}
	
	private void loadAllPathwaysByType(final String sXMLPath) 
	{	
	    File folder = new File(sXMLPath);
	    File[] arFiles = folder.listFiles();

	    for (int iFileIndex = 0; iFileIndex < arFiles.length; iFileIndex++) 
	    {		
	    	// Skip subversion files
	    	String sPathwayFilePath = arFiles[iFileIndex].toString();
	    	
	    	if (!sPathwayFilePath.endsWith(".xml") 
	    			&& !sPathwayFilePath.contains("h_"))
	    		continue;
	    	
			generalManager.getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
	    }
	}
}
