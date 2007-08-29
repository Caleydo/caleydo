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
import cerberus.view.opengl.canvas.histogram.GLCanvasHistogram2D;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHistogram2D 
extends ACmdCreate_GlCanvasUser {
	
	protected float [] fResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see cerberus.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	protected int iHistogramLevel = 50;
	
	protected String color;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectHistogram2D(
			final IGeneralManager refGeneralManager, 
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HISTOGRAM2D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		int iSizeTokens= token.countTokens();
		
		fResolution = 
			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
		
		iHistogramLevel = (int) fResolution[iSizeTokens-1];
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
	}



	@Override
	public void doCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		
		if ( iTargetCollectionSetId > -1 ) {
			canvas.setTargetSetId( iTargetCollectionSetId );
		}
		else 
		{
			refGeneralManager.getSingelton().logMsg( 
					"CmdGLObjectHistogram2D no set defined!", 
					LoggerType.STATUS);
		}
		canvas.setHistogramLength( iHistogramLevel );
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
