/**
 * 
 */
package org.caleydo.testing.command.data.filter;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.data.filter.CmdDataFilterMinMax;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.testing.testing_util.CoreStarter;


import junit.framework.TestCase;

/**
 * @author alexsb
 *
 */
public class CmdDataFiterMinMaxTest extends TestCase {

	
	
	private CoreStarter myCoreStarter;
	private IGeneralManager myGeneralManager;

	/**
	 * Starting a Caleydo Core
	 */
	protected void setUp() throws Exception {
		super.setUp();
		myCoreStarter = new CoreStarter();
		myCoreStarter.startCaleydoCore("");
		myGeneralManager = myCoreStarter.getGeneralManager();

	}

	/**
	 * Deleting a Caleydo Core
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		myCoreStarter.disposeCaleydoCore();
		
	}	

	public void testForFloat() 
	{		
		
		IStorage myStorage = myGeneralManager.getSingleton().getStorageManager().getItemStorage(46301);
		
		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) myGeneralManager.getSingleton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
		
		createdCmd.setAttributes(myStorage, StorageType.FLOAT);
		
		createdCmd.doCommand();		
		
		assertEquals(1524.0f, createdCmd.getFMinValue(), 0.001f);
		assertEquals(56131.0f, createdCmd.getFMaxValue(), 0.001f);
	}
	
	public void testForInt()
	{
		IStorage myStorage = myGeneralManager.getSingleton().getStorageManager().getItemStorage(45301);
		
		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) myGeneralManager.getSingleton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
		
		createdCmd.setAttributes(myStorage, StorageType.INT);
		
		createdCmd.doCommand();		
		
		assertEquals(618, createdCmd.getIMinValue());
		assertEquals(56432, createdCmd.getIMaxValue());
		
	}
	
	public void testForDouble()
	{
		IStorage myStorage = myGeneralManager.getSingleton().getStorageManager().getItemStorage(47301);
		
		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) myGeneralManager.getSingleton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
		
		createdCmd.setAttributes(myStorage, StorageType.DOUBLE);
		
		createdCmd.doCommand();		
		
		assertEquals(3626.0, createdCmd.getDMinValue(), 0.001);
		assertEquals(54981.0, createdCmd.getDMaxValue(), 0.001);
	}
	

}
