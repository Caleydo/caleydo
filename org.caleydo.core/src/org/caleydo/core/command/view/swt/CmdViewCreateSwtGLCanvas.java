package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;

/**
 * Class implements the command for creating a SWT-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateSwtGLCanvas 
extends ACmdCreate_IdTargetLabelParentAttrOpenGL {

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
	public void doCommand() throws CaleydoRuntimeException {
		
		try
		{
			IViewGLCanvasManager viewManager = ((IViewGLCanvasManager) generalManager
					.getManagerByObjectType(ManagerObjectType.VIEW));
			
			SwtJoglGLCanvasViewRep swtGLCanvasView = (SwtJoglGLCanvasViewRep)viewManager			
					.createGLView(ManagerObjectType.VIEW_SWT_JOGL_MULTI_GLCANVAS,
								iUniqueId, 
								iParentContainerId, 
								iGLCanvasID,
								sLabel );

			assert swtGLCanvasView != null : "SwtJoglCanvasViewRep could not be created!";
			
			/**
			 * Register this new SwtJoglGLCanvasViewRep to ViewManager...
			 */
			viewManager.registerItem(
					swtGLCanvasView, 
					iUniqueId, 
					ManagerObjectType.VIEW);
			
			
			swtGLCanvasView.setAttributes(iWidthX, iHeightY, iGLCanvasID);
			swtGLCanvasView.initViewSwtComposit(null);
			swtGLCanvasView.drawView();
			
			refCommandManager.runDoCommand(this);
			
		} 	
		catch ( CaleydoRuntimeException ce)
		{
//			generalManager.logMsg("Can not open Jogl frame inside SWT container! " + ce.toString(),
//					LoggerType.ERROR );
			ce.printStackTrace();
		}
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
//		/**
//		 * Same code as org.caleydo.core.command.base.ACmdCreate_IdTargetParentGLObject#setParameterHandler( final IParameterHandler refParameterHandler ) 
//		 * 
//		 */
//		String sPositionGLOrigin = refParameterHandler.getValueString( 
//				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
//		
//		String sPositionGLRotation = refParameterHandler.getValueString( 
//				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
//		
//		/* convert values.. */
//		if ( sPositionGLOrigin != null ) 
//		{
//			refParameterHandler.setValueAndTypeAndDefault( 
//					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey(),
//					sPositionGLOrigin, 
//					ParameterHandlerType.VEC3F,
//					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getDefault() );
//			
//			cameraOrigin_SWTGLCanvas = refParameterHandler.getValueVec3f( 
//					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
//		}
//		
//		if ( sPositionGLRotation != null ) 
//		{
//			refParameterHandler.setValueAndTypeAndDefault( 
//					CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey(),
//					sPositionGLRotation, 
//					ParameterHandlerType.VEC4F,
//					CommandQueueSaxType.TAG_POS_GL_ROTATION.getDefault() );
//			
//			/* convert Vec4f to roation Rotf */
//			Vec4f vec4fRotation = refParameterHandler.getValueVec4f( 
//					CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
//			
//			cameraRotation_SWTGLCanvas.set( new Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
//					MathUtil.grad2radiant(vec4fRotation.w()));
//		}
		
	}
	
	public void undoCommand() throws CaleydoRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
