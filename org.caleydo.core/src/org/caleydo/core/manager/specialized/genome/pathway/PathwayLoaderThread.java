package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.media.opengl.GLEventListener;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

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
		
		generalManager.getLogger().log(Level.INFO, "Start pathway databases loader thread");
		
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
			loadAllPathwaysByType(iterPathwayDatabase.next());
		}
		return;
	}
	
	private void loadAllPathwaysByType(final PathwayDatabase pathwayDatabase) 
	{	
//		// Try reading list of files directly from local hard dist
//	    File folder = new File(sXMLPath);
//	    File[] arFiles = folder.listFiles();
		
		BufferedReader file = null;
		String sLine = null;
		String sFileName = "";
		
		if (pathwayDatabase.getName().equals("KEGG"))
			sFileName = "data/genome/pathway/pathway_list_KEGG.txt";
		else if (pathwayDatabase.getName().equals("BioCarta"))
			sFileName = "data/genome/pathway/pathway_list_BIOCARTA.txt";
		
		try 
		{
		    if (this.getClass().getClassLoader().getResourceAsStream(sFileName) != null)
		    {
		    	file = new BufferedReader(new InputStreamReader(
		    				this.getClass().getClassLoader().getResourceAsStream(sFileName)));
		    }
		    else
		    {
		    	file = new BufferedReader(new FileReader(sFileName));
		    }
			
		    StringTokenizer tokenizer;
		    String sPathwayPath;
		    PathwayGraph tmpPathwayGraph;
			while ((sLine = file.readLine()) != null)
			{
				tokenizer = new StringTokenizer(sLine, " ");
				
				sPathwayPath = tokenizer.nextToken();
				
				// Skip non pathway files
		    	if (!sPathwayPath.endsWith(".xml") 
		    			&& !sLine.contains("h_"))
		    		continue;
		    	
				generalManager.getXmlParserManager().parseXmlFileByName(sPathwayPath);
				
				tmpPathwayGraph = generalManager.getPathwayManager().getCurrenPathwayGraph();
				tmpPathwayGraph.setWidth(StringConversionTool.convertStringToInt(tokenizer.nextToken(), -1));
				tmpPathwayGraph.setHeight(StringConversionTool.convertStringToInt(tokenizer.nextToken(), -1));	
			}
			
		} catch (FileNotFoundException e) {
			generalManager.getLogger().log(Level.SEVERE, "Pathway list file: " + sFileName + " not found.");
		} catch(IOException e) {
			generalManager.getLogger().log(Level.SEVERE, "Error reading data from pathway list file: " + sFileName);
		}
		
		for (GLEventListener tmpGLEventListener : 		
			generalManager.getViewGLCanvasManager().getAllGLEventListeners()) 
		{
			if (tmpGLEventListener.getClass().equals(GLCanvasRemoteRendering3D.class))
			{
				((GLCanvasRemoteRendering3D)tmpGLEventListener).enableBusyMode(false);
				break;
			}
				
		}
	}
}
