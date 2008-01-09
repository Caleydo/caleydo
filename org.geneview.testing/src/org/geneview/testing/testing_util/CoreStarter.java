package org.geneview.testing.testing_util;

import org.geneview.core.application.core.GeneViewBootloader;
import org.geneview.core.manager.IGeneralManager;

public class CoreStarter {
	
	//private IGeneralManager refGeneralManager;	
	
	private GeneViewBootloader geneviewCore;
	
	
	/**
	 * Starts GeneView with the specified XML File. A simple sample is used 
	 * if no XML file is specified. Returns the core, which can be also accessed
	 * with getGeneViewCore
	 * @param xmlFileName
	 * @return
	 */
	public GeneViewBootloader startGeneViewCore( final String xmlFileName ) 
	{
		
		geneviewCore = new GeneViewBootloader();
			
		if  (xmlFileName=="") 
		{
			geneviewCore.setXmlFileName(
				"data/bootstrap/bootstrap_sample_basic_unit_test.xml"); 	
		}

		geneviewCore.run_SWT();
		
		return geneviewCore;
	}
	
	/**
	 * Deletes the core
	 */
	public void disposeGeneViewCore() {
		
		System.out.println(getClass().getSimpleName() + ".disposeGeneViewCore() shutdown ...");
		
		if ( geneviewCore != null ) 
		{
			if ( geneviewCore.isRunning() ) 
			{
				geneviewCore.stop();
				geneviewCore = null;
			}
			else 
			{
				System.err.println(getClass().getSimpleName() + ".disposeGeneViewCore() core was already stopped!");
			}
		}
	}
	
	public GeneViewBootloader getGeneViewCore()
	{
		return geneviewCore;		
	}
	
	public IGeneralManager getGeneralManager()
	{
		return geneviewCore.getGeneralManager();
	}

}
