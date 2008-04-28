package org.caleydo.testing.command.data.filter;

import java.util.ArrayList;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.data.filter.CmdDataFilterMath;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.testing.testing_util.CoreStarter;

import junit.framework.TestCase;

public class CmdDataFilterMathTest extends TestCase {

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
	
	public void testNormalizeOverwiteFloat()
	{	
		CmdDataFilterMath createdCmd = (CmdDataFilterMath) myGeneralManager
			.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MATH);
		
		ArrayList<Integer> floatStorageIDs = new ArrayList<Integer>();
		floatStorageIDs.add(46301);
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.NORMALIZE,
								floatStorageIDs);
		
		createdCmd.doCommand();	
		
		IStorage myStorage = myGeneralManager.getStorageManager().getItemStorage(46301);
		assertEquals(0.453824601f, myStorage.getArrayFloat()[0], 0.000001f);
		assertEquals(0.541304228f, myStorage.getArrayFloat()[1], 0.000001f);
		assertEquals(0.937810171f, myStorage.getArrayFloat()[2], 0.000001f);
		// the maximum is in this line
		assertEquals(1.0f, myStorage.getArrayFloat()[3], 0.000001f);
		// the minimum is in this line
		assertEquals(0.0f, myStorage.getArrayFloat()[8], 0.000001f);		
		
	}
	
	public void testNormalizeCopyFloat()
	{	
		CmdDataFilterMath createdCmd = (CmdDataFilterMath) myGeneralManager
			.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MATH);
		
		ArrayList<Integer> floatStorageIDs = new ArrayList<Integer>();
		floatStorageIDs.add(46301);		
		
		IStorage myTargetStorage = myGeneralManager
			.getStorageManager().createStorage(ManagerObjectType.STORAGE_FLAT);
		myGeneralManager.getStorageManager()
			.registerItem(myTargetStorage, myTargetStorage.getId(), ManagerObjectType.STORAGE_FLAT);		
		
		ArrayList<Integer> iAlFloatTargetStorageIDs = new ArrayList<Integer>();
		iAlFloatTargetStorageIDs.add(myTargetStorage.getId());
		
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.NORMALIZE,
				floatStorageIDs, iAlFloatTargetStorageIDs);
		
		createdCmd.doCommand();	
		
		assertEquals(0.453824601f, myTargetStorage.getArrayFloat()[0], 0.000001f);
		assertEquals(0.541304228f, myTargetStorage.getArrayFloat()[1], 0.000001f);
		assertEquals(0.937810171f, myTargetStorage.getArrayFloat()[2], 0.000001f);
		// the maximum is in this line
		assertEquals(1.0f, myTargetStorage.getArrayFloat()[3], 0.000001f);
		// the minimum is in this line
		assertEquals(0.0f, myTargetStorage.getArrayFloat()[8], 0.000001f);		
		
	}
	
	public void testLinToLogOverwriteFloat()
	{	
		CmdDataFilterMath createdCmd = (CmdDataFilterMath) myGeneralManager
			.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MATH);
		
		ArrayList<Integer> floatStorageIDs = new ArrayList<Integer>();
		floatStorageIDs.add(46301);
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.LIN_2_LOG,
								floatStorageIDs);
		
		createdCmd.doCommand();	
		
		IStorage myStorage = myGeneralManager.getStorageManager().getItemStorage(46301);
		assertEquals(4.42005482f, myStorage.getArrayFloat()[0], 0.001f);
		assertEquals(4.49252293f, myStorage.getArrayFloat()[1], 0.001f);
		assertEquals(4.72209895f, myStorage.getArrayFloat()[2], 0.001f);
		// the maximum is in this line
		assertEquals(4.74920278f, myStorage.getArrayFloat()[3], 0.001f);
		// the minimum is in this line
		assertEquals(3.18298497f, myStorage.getArrayFloat()[8], 0.001f);		
		
	}
	
	public void testLinToLogCopyFloat()
	{
		CmdDataFilterMath createdCmd = (CmdDataFilterMath) myGeneralManager
			.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MATH);
		
		ArrayList<Integer> floatStorageIDs = new ArrayList<Integer>();
		floatStorageIDs.add(46301);		
		
		IStorage myTargetStorage = myGeneralManager
			.getStorageManager().createStorage(ManagerObjectType.STORAGE_FLAT);
		myGeneralManager.getStorageManager()
			.registerItem(myTargetStorage, myTargetStorage.getId(), ManagerObjectType.STORAGE_FLAT);		
		
		ArrayList<Integer> iAlFloatTargetStorageIDs = new ArrayList<Integer>();
		iAlFloatTargetStorageIDs.add(myTargetStorage.getId());
		
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.LIN_2_LOG,
				floatStorageIDs, iAlFloatTargetStorageIDs);
		
		createdCmd.doCommand();	
		
		assertEquals(4.42005482f, myTargetStorage.getArrayFloat()[0], 0.001f);
		assertEquals(4.49252293f, myTargetStorage.getArrayFloat()[1], 0.001f);
		assertEquals(4.72209895f, myTargetStorage.getArrayFloat()[2], 0.001f);
		// the maximum is in this line
		assertEquals(4.74920278f, myTargetStorage.getArrayFloat()[3], 0.001f);
		// the minimum is in this line
		assertEquals(3.18298497f, myTargetStorage.getArrayFloat()[8], 0.001f);		
		
	}	
	
	public void testLogToLinOverwriteFloat()
	{
		CmdDataFilterMath createdCmd = (CmdDataFilterMath) myGeneralManager
			.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MATH);
		
		ArrayList<Integer> floatStorageIDs = new ArrayList<Integer>();
		floatStorageIDs.add(46301);
		
		
		IStorage myOriginalStorage = myGeneralManager
		.getStorageManager().createStorage(ManagerObjectType.STORAGE_FLAT);

		myOriginalStorage.setArrayFloat(myGeneralManager.getStorageManager().getItemStorage(46301).getArrayFloat());
		
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.LIN_2_LOG,
								floatStorageIDs);
		
		createdCmd.doCommand();	
		
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.LOG_2_LIN,
				floatStorageIDs);

		createdCmd.doCommand();	
		
		
		IStorage myStorage = myGeneralManager.getStorageManager().getItemStorage(46301);
	
		assertEquals(myOriginalStorage.getArrayFloat()[0], myStorage.getArrayFloat()[0], 2.0f);
		assertEquals(myOriginalStorage.getArrayFloat()[1], myStorage.getArrayFloat()[1], 2.0f);
		assertEquals(myOriginalStorage.getArrayFloat()[2], myStorage.getArrayFloat()[2], 2.0f);
		// the maximum is in this line
		assertEquals(myOriginalStorage.getArrayFloat()[3], myStorage.getArrayFloat()[3], 2.0f);
		// the minimum is in this line
		assertEquals(myOriginalStorage.getArrayFloat()[8], myStorage.getArrayFloat()[8], 2.0f);		
		
	}
	
	public void testLogToLinCopyFloat()
	{
			
		CmdDataFilterMath createdCmd = (CmdDataFilterMath) myGeneralManager
			.getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MATH);
		
		ArrayList<Integer> floatStorageIDs = new ArrayList<Integer>();
		floatStorageIDs.add(46301);
		
		
		IStorage myOriginalStorage = myGeneralManager
		.getStorageManager().createStorage(ManagerObjectType.STORAGE_FLAT);

		myOriginalStorage.setArrayFloat(myGeneralManager.getStorageManager().getItemStorage(46301).getArrayFloat());
		
		IStorage myTargetStorage = myGeneralManager
		.getStorageManager().createStorage(ManagerObjectType.STORAGE_FLAT);
		myGeneralManager.getStorageManager()
		.registerItem(myTargetStorage, myTargetStorage.getId(), ManagerObjectType.STORAGE_FLAT);		
	
		ArrayList<Integer> iAlFloatTargetStorageIDs = new ArrayList<Integer>();
		iAlFloatTargetStorageIDs.add(myTargetStorage.getId());
	
		
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.LIN_2_LOG,
				floatStorageIDs);
		
		
		createdCmd.doCommand();	
		
		createdCmd.setAttributes(CmdDataFilterMath.EDataFilterMathType.LOG_2_LIN,
		floatStorageIDs, iAlFloatTargetStorageIDs);

		createdCmd.doCommand();	
		
		assertEquals(myOriginalStorage.getArrayFloat()[0], myTargetStorage.getArrayFloat()[0], 2.0f);
		assertEquals(myOriginalStorage.getArrayFloat()[1], myTargetStorage.getArrayFloat()[1], 2.0f);
		assertEquals(myOriginalStorage.getArrayFloat()[2], myTargetStorage.getArrayFloat()[2], 2.0f);
		// the maximum is in this line
		assertEquals(myOriginalStorage.getArrayFloat()[3], myTargetStorage.getArrayFloat()[3], 2.0f);
		// the minimum is in this line
		assertEquals(myOriginalStorage.getArrayFloat()[8], myTargetStorage.getArrayFloat()[8], 2.0f);		
	}
}
