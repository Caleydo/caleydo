package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdViewCreateTestTriangle 
extends ACmdCreate_IdTargetLabelParentXY 
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
		
		triangleView.readInAttributes(refParameterHandler);
		
		triangleView.retrieveGUIContainer();
		triangleView.initView();
		triangleView.drawView();	
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}
}
