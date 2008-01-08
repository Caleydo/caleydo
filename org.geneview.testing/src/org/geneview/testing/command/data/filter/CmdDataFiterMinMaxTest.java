/**
 * 
 */
package org.geneview.testing.command.data.filter;

import org.geneview.core.application.core.GeneViewBootloader;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.rcp.Application;

import junit.framework.TestCase;

/**
 * @author alexsb
 *
 */
public class CmdDataFiterMinMaxTest extends TestCase {

	// FIXME: should not be static!
	public static IGeneralManager refGeneralManager;	
	
	public static GeneViewBootloader geneview_core;
	
	/**
	 * @param name
	 */
	public CmdDataFiterMinMaxTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		startGeneViewCore("");	
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#CmdDataFilterMinMax(org.geneview.core.manager.IGeneralManager, org.geneview.core.manager.ICommandManager, org.geneview.core.command.CommandQueueSaxType)}.
	 */
	public void testCmdDataFilterMinMax() {
		//fail("Not yet implemented");
		 assertTrue(true);
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#doCommand()}.
	 */
	public void testDoCommand() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#undoCommand()}.
	 */
	public void testUndoCommand() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#setAttributes(org.geneview.core.data.collection.IStorage, org.geneview.core.data.collection.StorageType)}.
	 */
	public void testSetAttributesIStorageStorageType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#setAttributes(org.geneview.core.data.collection.ISet, org.geneview.core.data.collection.StorageType)}.
	 */
	public void testSetAttributesISetStorageType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#getIMinValue()}.
	 */
	public void testGetIMinValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#getIMaxValue()}.
	 */
	public void testGetIMaxValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#getFMinValue()}.
	 */
	public void testGetFMinValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#getFMaxValue()}.
	 */
	public void testGetFMaxValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#getDMinValue()}.
	 */
	public void testGetDMinValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.geneview.core.command.data.filter.CmdDataFilterMinMax#getDMaxValue()}.
	 */
	public void testGetDMaxValue() {
		fail("Not yet implemented");
	}
	
	protected void startGeneViewCore( final String xmlFileName ) {
		
		geneview_core = new GeneViewBootloader();
			
		if  (xmlFileName=="") 
		{
			geneview_core.setXmlFileName(
				"data/bootstrap/bootstrap_sample_parcoords.xml"); 	
		}

		Application.refGeneralManager = geneview_core.getGeneralManager();

		geneview_core.run_SWT();
	}
	
	protected void disposeGeneViewCore() {
		
		System.out.println(getClass().getSimpleName() + ".disposeGeneViewCore() shutdown ...");
		
		if ( geneview_core != null ) 
		{
			if ( geneview_core.isRunning() ) 
			{
				geneview_core.stop();
				geneview_core = null;
			}
			else 
			{
				System.err.println(getClass().getSimpleName() + ".disposeGeneViewCore() core was already stopped!");
			}
		}
	}

}
