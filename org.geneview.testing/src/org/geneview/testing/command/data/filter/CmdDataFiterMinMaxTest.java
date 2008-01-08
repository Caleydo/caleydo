/**
 * 
 */
package org.geneview.testing.command.data.filter;
import org.geneview.core.application.core.GeneViewBootloader;
import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.data.filter.CmdDataFilterMinMax;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.StorageType;
import org.geneview.rcp.Application;
import org.geneview.core.manager.IGeneralManager;

import junit.framework.TestCase;

/**
 * @author alexsb
 *
 */
public class CmdDataFiterMinMaxTest extends TestCase {

	
	
	/**
	 * @param name
	 */
	public CmdDataFiterMinMaxTest(String name) {
		super(name);
	}
	private GeneViewBootloader geneviewCore;
	private IGeneralManager myGeneralManager;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		geneviewCore = new GeneViewBootloader();
		
		//if  (xmlFileName=="") 
		//{
			geneviewCore.setXmlFileName(
				"data/bootstrap/bootstrap_sample_basic_unit_test.xml"); 	
		//}

		Application.refGeneralManager = geneviewCore.getGeneralManager();

		geneviewCore.run_SWT();
		
		myGeneralManager = geneviewCore.getGeneralManager();
	
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		//myStarter.disposeGeneViewCore();
		
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

	public void testForFloat() 
	{		
		
		IStorage myStorage = myGeneralManager.getSingelton().getStorageManager().getItemStorage(46301);
		
		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) myGeneralManager.getSingelton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
		
		createdCmd.setAttributes(myStorage, StorageType.FLOAT);
		
		createdCmd.doCommand();		
		
		assertEquals(1524.0f, createdCmd.getFMinValue(), 0.001f);
		assertEquals(56131.0f, createdCmd.getFMaxValue(), 0.001f);
	}
	
	public void testForInt()
	{
		IStorage myStorage = myGeneralManager.getSingelton().getStorageManager().getItemStorage(45301);
		
		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) myGeneralManager.getSingelton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
		
		createdCmd.setAttributes(myStorage, StorageType.INT);
		
		createdCmd.doCommand();		
		
		assertEquals(618, createdCmd.getIMinValue());
		assertEquals(56432, createdCmd.getIMaxValue());
		
	}
	
	public void testForDouble()
	{
		IStorage myStorage = myGeneralManager.getSingelton().getStorageManager().getItemStorage(47301);
		
		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) myGeneralManager.getSingelton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
		
		createdCmd.setAttributes(myStorage, StorageType.DOUBLE);
		
		createdCmd.doCommand();		
		
		assertEquals(3626.0, createdCmd.getDMinValue(), 0.001);
		assertEquals(54981.0, createdCmd.getDMaxValue(), 0.001);
	}
	

}
