package org.caleydo.testing.testing_util;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.IGeneralManager;

public class CoreStarter {
	
	//private IGeneralManager refGeneralManager;	
	
	private CaleydoBootloader geneviewCore;
	
	
	/**
	 * Starts Caleydo with the specified XML File. A simple sample is used 
	 * if no XML file is specified. Returns the core, which can be also accessed
	 * with getCaleydoCore
	 * @param xmlFileName
	 * @return
	 */
	public CaleydoBootloader startCaleydoCore( final String xmlFileName ) 
	{
		
		geneviewCore = new CaleydoBootloader();
			
		if  (xmlFileName=="") 
		{
			geneviewCore.setXmlFileName(
				"data/bootstrap/shared/testing/bootstrap_sample_basic_unit_test.xml"); 	
		}

		geneviewCore.run_SWT();
		
		return geneviewCore;
	}
	
	/**
	 * Deletes the core
	 */
	public void disposeCaleydoCore() {
		
		System.out.println(getClass().getSimpleName() + ".disposeCaleydoCore() shutdown ...");
		
		if ( geneviewCore != null ) 
		{
			if ( geneviewCore.isRunning() ) 
			{
				geneviewCore.stop();
				geneviewCore = null;
			}
			else 
			{
				System.err.println(getClass().getSimpleName() + ".disposeCaleydoCore() core was already stopped!");
			}
		}
	}
	
	public CaleydoBootloader getCaleydoCore()
	{
		return geneviewCore;		
	}
	
	public IGeneralManager getGeneralManager()
	{
		return geneviewCore.getGeneralManager();
	}

}
