package org.geneview.core.command.view.rcp;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.geneview.core.data.view.camera.IViewCamera;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.math.MathUtil;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.jogl.JoglCanvasForwarder;
import org.geneview.core.view.jogl.JoglCanvasForwarderType;
import org.geneview.core.view.opengl.RcpCanvasDirector;
import org.geneview.core.manager.ILoggerManager.LoggerType;

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
		
		RcpCanvasDirector rcpCanvasDirector = null;
		
		try
		{
			rcpCanvasDirector = new RcpCanvasDirector(iUniqueId, iGlForwarderId,
					refGeneralManager, JoglCanvasForwarderType.ONLY_2D_FORWARDER);
		}
		catch (NoClassDefFoundError ncde) 
		{
			String errorMsg = "missing class; most probably jogl.jar is missing; can not create OpenGL frame; ";
			refGeneralManager.getSingelton().logMsg(
					errorMsg + ncde.toString(), 
					LoggerType.ERROR);
			
			throw new GeneViewRuntimeException(errorMsg);
		}
		
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
