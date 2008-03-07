package org.geneview.core.command.base;

import java.util.StringTokenizer;

import javax.media.opengl.GLEventListener;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.camera.ViewFrustumBase;
import org.geneview.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.system.StringConversionTool;

/**
 * 
 * Command for creating a GL event listener
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ACmdCreate_GlCanvasUser 
extends ACmdCreate_IdTargetParentGLObject {
	
	protected CommandQueueSaxType localManagerObjectType;
	
	protected GLEventListener gLEventListener;
	
	protected IViewFrustum viewFrustum;
	
	/**
	 *
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_GlCanvasUser(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		// Create default view frustum
		viewFrustum = new ViewFrustumBase(ProjectionMode.ORTHOGRAPHIC,
				-4f, 4f, -4f, 4f, -20f, 20f);
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetParentGLObject#setParameterHandler(org.geneview.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( 
			final IParameterHandler refParameterHandler) {
		
		super.setParameterHandler(refParameterHandler);
		
		StringTokenizer frustumToken = new StringTokenizer(
				sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);

		try
		{
			// Parse projection mode (PERSPECTIVE / ORTHOGRAPHIC)
			String sProjectionMode = "";
			
			if (frustumToken.hasMoreTokens())
				sProjectionMode = frustumToken.nextToken();
	
			if (!sProjectionMode.equals(ViewFrustumBase.ProjectionMode.ORTHOGRAPHIC.name())
				&& !sProjectionMode.equals(ViewFrustumBase.ProjectionMode.PERSPECTIVE.name()))
			{
				return;
			}
			
			float fLeft = -1;
			float fRight = -1;
			float fBottom = -1;
			float fTop = -1;
			float fNear = -1;
			float fFar = -1;
		
			fLeft = StringConversionTool.convertStringToFloat(
					frustumToken.nextToken(), -1);
			
			fRight = StringConversionTool.convertStringToFloat(
					frustumToken.nextToken(), -1);
			
			fBottom = StringConversionTool.convertStringToFloat(
					frustumToken.nextToken(), -1);
			
			fTop = StringConversionTool.convertStringToFloat(
					frustumToken.nextToken(), -1);
			
			fNear = StringConversionTool.convertStringToFloat(
					frustumToken.nextToken(), -1);
			
			fFar = StringConversionTool.convertStringToFloat(
					frustumToken.nextToken(), -1);
			
			viewFrustum = new ViewFrustumBase(ViewFrustumBase.ProjectionMode.valueOf(sProjectionMode), 
					fLeft, fRight, fBottom, fTop, fNear, fFar);
		
		}catch (Exception e) {
			
			generalManager.getSingelton()
				.logMsg(this.getClass().getSimpleName()
							+ ": Error in extracting view frustum from XML argument!",
					LoggerType.ERROR);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand()
	{
		IViewGLCanvasManager glCanvasManager = 
			generalManager.getSingelton().getViewGLCanvasManager();
		
		gLEventListener = glCanvasManager.createGLCanvas(
				localManagerObjectType,
				iUniqueId,
				iParentContainerId,
				sLabel,
				viewFrustum);

		refCommandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand()
	{

		//TODO: Proper cleanup
		
		refCommandManager.runUndoCommand(this);
	}
}
