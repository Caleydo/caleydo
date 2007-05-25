package cerberus.command.view.swt;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.math.MathUtil;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * Class implementes the command for creating a SWT-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateSwtGLCanvas 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	protected Vec3f cameraOrigin_SWTGLCanvas;
	
	protected Rotf cameraRotation_SWTGLCanvas;
	
	protected int iGLCanvasId = 0;
	 
	protected int iGLEventListernerId = 0;
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateSwtGLCanvas(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
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
			swtGLCanvasView.initView();
			swtGLCanvasView.drawView();
			
			IViewCamera refViewCamera_CanvasForwarder = 
				swtGLCanvasView.getJoglCanvasForwarder().getViewCamera();
			
			if ( cameraOrigin_SWTGLCanvas != null ) 
				refViewCamera_CanvasForwarder.setCameraPosition(cameraOrigin_SWTGLCanvas);
			
			if ( cameraRotation_SWTGLCanvas != null)
				refViewCamera_CanvasForwarder.setCameraRotation(cameraRotation_SWTGLCanvas);
			
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
		
		/**
		 * Same code as cerberus.command.base.ACmdCreate_IdTargetParentGLObject#setParameterHandler( final IParameterHandler refParameterHandler ) 
		 * 
		 */
		String sPositionGLOrigin = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		
		String sPositionGLRotation = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		
		/* convert values.. */
		if ( sPositionGLOrigin != null ) 
		{
			refParameterHandler.setValueAndTypeAndDefault( 
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey(),
					sPositionGLOrigin, 
					ParameterHandlerType.VEC3F,
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getDefault() );
			
			cameraOrigin_SWTGLCanvas = refParameterHandler.getValueVec3f( 
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		}
		
		if ( sPositionGLRotation != null ) 
		{
			refParameterHandler.setValueAndTypeAndDefault( 
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey(),
					sPositionGLRotation, 
					ParameterHandlerType.VEC4F,
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getDefault() );
			
			/* convert Vec4f to roation Rotf */
			Vec4f vec4fRotation = refParameterHandler.getValueVec4f( 
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
			
			cameraRotation_SWTGLCanvas.set( new Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
					MathUtil.grad2radiant(vec4fRotation.w()));
		}
		
	}
	
	public void undoCommand() throws CerberusRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
