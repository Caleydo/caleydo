package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateSwtGLCanvas 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	protected int iGLCanvasId = 0;
	 
	protected int iGLEventListernerId = 0;
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	public CmdViewCreateSwtGLCanvas(final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager) {
		
		super(refGeneralManager, refCommandManager);
	}
	
	/**
	 * Method creates a test triangle view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		try
		{
			IViewGLCanvasManager viewManager = ((IViewGLCanvasManager) refGeneralManager
					.getManagerByBaseType(ManagerObjectType.VIEW));
			
			SwtJoglGLCanvasViewRep swtGLCanvasView = (SwtJoglGLCanvasViewRep)viewManager
					.createView(ManagerObjectType.VIEW_SWT_JOGL_MULTI_GLCANVAS,
								iUniqueTargetId, 
								iParentContainerId, 
								sLabel);

			assert swtGLCanvasView != null : "SwtJoglCanvasViewRep could not be created!";
			
			/**
			 * Register this new SwtJoglGLCanvasViewRep to ViewManager...
			 */
			viewManager.registerItem(
					swtGLCanvasView, 
					iUniqueTargetId, 
					ManagerObjectType.VIEW);
			
			
			swtGLCanvasView.setAttributes(iWidthX, iHeightY, iGLCanvasId, iGLEventListernerId);
			swtGLCanvasView.retrieveGUIContainer();
			swtGLCanvasView.initView();
			swtGLCanvasView.drawView();
			
			refCommandManager.runDoCommand(this);
			
		} 	
		catch ( CerberusRuntimeException ce)
		{
			refGeneralManager.getSingelton().logMsg("Can not open Jogl frame inside SWT container! " + ce.toString(),
					LoggerType.ERROR_ONLY );
			ce.printStackTrace();
		}
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		iGLCanvasId = refParameterHandler.getValueInt(
				CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() );
		
		iGLEventListernerId = refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_GLCANVAS_LISTENER.getXmlKey() );

	}
	
	public void undoCommand() throws CerberusRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
