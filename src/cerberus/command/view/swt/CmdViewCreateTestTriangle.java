package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreateGui;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateTestTriangle 
extends ACmdCreateGui 
implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateTestTriangle( 
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) 
	{
		super(refGeneralManager, refParameterHandler);
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
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				triangleView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		triangleView.setAttributes(refParameterHandler);
		
		triangleView.retrieveGUIContainer();
		triangleView.initView();
		triangleView.drawView();	
	}
}
