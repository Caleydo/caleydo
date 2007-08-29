/**
 * 
 */
package cerberus.command.view.opengl;

import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.opengl.canvas.heatmap.GLCanvasHeatmap;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHeatmap 
extends ACmdCreate_GlCanvasUser {
	
	protected int [] iResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see cerberus.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	protected String color;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectHeatmap(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);

		iResolution = new int [3];
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HEATMAP;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		int i=0;		
		for ( ; token.hasMoreTokens(); i++ ) {
			iResolution[i] = StringConversionTool.convertStringToInt( 
					token.nextToken(), 
					0 );
		}
		
		if ( i != 3 ) {
			String logMessage = "CmdGlObjectHeatmap.setAttributesHeatmapWidthHeight() failed! 3 values are excpected, but only " +
			i + " are present";
			
			refGeneralManager.getSingelton().logMsg(
					logMessage,
					LoggerType.MINOR_ERROR );
			
			throw new GeneViewRuntimeException( logMessage );
		}
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
	}



	@Override
	public void doCommandPart() throws GeneViewRuntimeException
	{

		GLCanvasHeatmap canvas = 
			(GLCanvasHeatmap) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( iResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
		
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException
	{
		
	}
	
	
}
