/**
 * 
 */
package org.geneview.testing.command.data.filter;
import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.data.filter.CmdDataFilterMinMax;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.StorageType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.testing.testing_util.CoreStarter;


import junit.framework.TestCase;

/**
 * @author alexsb
 *
 */
public class CmdDataFiterMinMaxTest extends TestCase {

	
	
	private CoreStarter myCoreStarter;
	private IGeneralManager myGeneralManager;

	/**
	 * Starting a GeneView Core
	 */
	protected void setUp() throws Exception {
		super.setUp();
		myCoreStarter = new CoreStarter();
		myCoreStarter.startGeneViewCore("");
		myGeneralManager = myCoreStarter.getGeneralManager();

	}

	/**
	 * Deleting a GeneView Core
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		myCoreStarter.disposeGeneViewCore();
		
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
