package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.AcmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateSwtGLCanvas 
extends AcmdCreate_IdTargetLabelParentXY 
implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateSwtGLCanvas( 
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
		IViewGLCanvasManager viewManager = ((IViewGLCanvasManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		SwtJoglGLCanvasViewRep swtGLCanvasView = (SwtJoglGLCanvasViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_JOGL_MULTI_GLCANVAS,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				swtGLCanvasView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		swtGLCanvasView.setAttributes(refParameterHandler);
		
		swtGLCanvasView.retrieveGUIContainer();
		swtGLCanvasView.initView();
		swtGLCanvasView.drawView();	
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}
}
