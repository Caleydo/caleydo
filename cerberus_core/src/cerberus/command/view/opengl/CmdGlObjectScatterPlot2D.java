/**
 * 
 */
package cerberus.command.view.opengl;


import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.parser.parameter.IParameterHandler;
//import cerberus.manager.ILoggerManager.LoggerType;
//import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
//import cerberus.view.opengl.canvas.scatterplot.GLCanvasScatterPlot2D;
import cerberus.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn;
import cerberus.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D;
import cerberus.view.opengl.canvas.scatterplot.GLMinMaxScatterplot2Dinteractive;

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
	 * @see cerberus.data.collection.ISet
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
