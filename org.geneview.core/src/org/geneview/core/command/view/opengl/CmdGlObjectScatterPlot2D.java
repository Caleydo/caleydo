/**
 * 
 */
package org.geneview.core.command.view.opengl;


import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
//import org.geneview.core.manager.ILoggerManager.LoggerType;
//import org.geneview.core.manager.command.factory.CommandFactory;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;
//import org.geneview.core.view.opengl.canvas.scatterplot.GLCanvasScatterPlot2D;
import org.geneview.core.view.opengl.canvas.scatterplot.GLMinMaxScatterplot2Dinteractive;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectScatterPlot2D 
extends ACmdCreate_GlCanvasUser {
	
	protected int[] iResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see org.geneview.core.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	protected String color;
	
	protected float [] fResolution;
	
	/**
	 * Constructur.
	 * 
	 */
	public CmdGlObjectScatterPlot2D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		iResolution = new int[3];
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_SCATTERPLOT2D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);
		int iSizeTokens= token.countTokens();
		
		fResolution = 
			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				this.sDetail, 
				-1 );
		
		iResolution = StringConversionTool.convertStringToIntArray(
				refGeneralManager.getSingelton().getLoggerManager(), 
				sAttribute3,
				3 );
		
	}



	@Override
	public void doCommandPart() throws GeneViewRuntimeException
	{
		GLMinMaxScatterplot2Dinteractive canvas = 
			(GLMinMaxScatterplot2Dinteractive) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException
	{
		GLMinMaxScatterplot2Dinteractive canvas =
			(GLMinMaxScatterplot2Dinteractive) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
