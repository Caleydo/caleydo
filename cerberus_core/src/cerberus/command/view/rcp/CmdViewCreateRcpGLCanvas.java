package cerberus.command.view.rcp;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.math.MathUtil;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.parser.parameter.IParameterHandler.ParameterHandlerType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.jogl.JoglCanvasForwarder;
import cerberus.view.jogl.JoglCanvasForwarderType;
import cerberus.view.opengl.RcpCanvasDirector;

/**
 * Class implements the command for creating a RCP-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateRcpGLCanvas 
extends ACmdCreate_IdTargetLabelParentAttrOpenGL {
	
	protected Vec3f cameraOrigin_GLCanvas;
	
	protected Rotf cameraRotation_GLCanvas;

	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateRcpGLCanvas(
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
	public void doCommand() throws GeneViewRuntimeException {
		
			RcpCanvasDirector rcpCanvasDirector = new RcpCanvasDirector(iUniqueId, iGlForwarderId,
					refGeneralManager, JoglCanvasForwarderType.ONLY_2D_FORWARDER);

			IViewGLCanvasManager viewManager = refGeneralManager.getSingelton().getViewGLCanvasManager();
			
			viewManager.registerItem(
					rcpCanvasDirector, 
					iUniqueId, 
					ManagerObjectType.VIEW);
			
			JoglCanvasForwarder canvasForwarder = rcpCanvasDirector.getJoglCanvasForwarder();

			IViewCamera viewCamera = canvasForwarder.getViewCamera();
			
			if ( cameraOrigin_GLCanvas != null ) 
				viewCamera.setCameraPosition(cameraOrigin_GLCanvas);
			
			if ( cameraRotation_GLCanvas != null)
				viewCamera.setCameraRotation(cameraRotation_GLCanvas);
			
			refCommandManager.runDoCommand(this);
			
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
//		iGLCanvasId = refParameterHandler.getValueInt(
//				CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() );
//		
//		iGlForwarderId = refParameterHandler.getValueInt( 
//				CommandQueueSaxType.TAG_GLCANVAS_FORWARDER.getXmlKey() );

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
			
			cameraOrigin_GLCanvas = refParameterHandler.getValueVec3f( 
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
			
			cameraRotation_GLCanvas.set( new Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
					MathUtil.grad2radiant(vec4fRotation.w()));
		}
		
	}
	
	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
