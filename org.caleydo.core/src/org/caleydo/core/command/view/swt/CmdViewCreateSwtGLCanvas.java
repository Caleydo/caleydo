package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;

/**
 * Class implements the command for creating a SWT-Jogl canvas.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateSwtGLCanvas
	extends ACmdCreate_IdTargetLabelParentAttrOpenGL
{
	/**
	 * Constructor.
	 */
	public CmdViewCreateSwtGLCanvas(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		try
		{
			IViewManager viewManager = generalManager.getViewGLCanvasManager();

			SwtJoglGLCanvasViewRep swtGLCanvasView = (SwtJoglGLCanvasViewRep) viewManager
					.createGLView(EManagedObjectType.VIEW_SWT_JOGL_MULTI_GLCANVAS, iExternalID,
							iParentContainerId, iGLCanvasID, sLabel);

			assert swtGLCanvasView != null : "SwtJoglCanvasViewRep could not be created!";

			/**
			 * Register this new SwtJoglGLCanvasViewRep to ViewManager...
			 */
			viewManager.registerItem(swtGLCanvasView, iExternalID);

			swtGLCanvasView.setAttributes(iWidthX, iHeightY, iGLCanvasID);
			swtGLCanvasView.initViewSwtComposit(null);
			swtGLCanvasView.drawView();

			commandManager.runDoCommand(this);

		}
		catch (CaleydoRuntimeException ce)
		{
			// generalManager.logMsg(
			// "Can not open Jogl frame inside SWT container! " + ce.toString(),
			// LoggerType.ERROR );
			ce.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		// /**
		// * Same code as
		// org.caleydo.core.command.base.ACmdCreate_IdTargetParentGLObject
		// #setParameterHandler( final IParameterHandler parameterHandler )
		// *
		// */
		// String sPositionGLOrigin = parameterHandler.getValueString(
		// CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		//		
		// String sPositionGLRotation = parameterHandler.getValueString(
		// CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		//		
		// /* convert values.. */
		// if ( sPositionGLOrigin != null )
		// {
		// parameterHandler.setValueAndTypeAndDefault(
		// CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey(),
		// sPositionGLOrigin,
		// ParameterHandlerType.VEC3F,
		// CommandQueueSaxType.TAG_POS_GL_ORIGIN.getDefault() );
		//			
		// cameraOrigin_SWTGLCanvas = parameterHandler.getValueVec3f(
		// CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		// }
		//		
		// if ( sPositionGLRotation != null )
		// {
		// parameterHandler.setValueAndTypeAndDefault(
		// CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey(),
		// sPositionGLRotation,
		// ParameterHandlerType.VEC4F,
		// CommandQueueSaxType.TAG_POS_GL_ROTATION.getDefault() );
		//			
		// /* convert Vec4f to roation Rotf */
		// Vec4f vec4fRotation = parameterHandler.getValueVec4f(
		// CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		//			
		// cameraRotation_SWTGLCanvas.set( new
		// Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
		// MathUtil.grad2radiant(vec4fRotation.w()));
		// }

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
