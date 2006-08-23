package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateTestTriangle 
extends ACmdCreate 
implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateTestTriangle( 
			IGeneralManager refGeneralManager,
			final LinkedList <String> listAttributes) 
	{
		super(refGeneralManager, listAttributes);
	}
	
	/**
	 * Method creates a test triangle view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		TestTriangleViewRep triangleView = (TestTriangleViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_JOGL_TEST_TRIANGLE,
							iCreatedObjectId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				triangleView, 
				iCreatedObjectId, 
				ManagerObjectType.VIEW);
		
		triangleView.setAttributes(refVecAttributes);
		triangleView.extractAttributes();
		triangleView.retrieveGUIContainer();
		triangleView.initView();
		triangleView.drawView();	
	}
}
