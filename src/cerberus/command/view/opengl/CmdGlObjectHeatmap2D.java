/**
 * 
 */
package cerberus.command.view.opengl;

import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.heatmap.GLCanvasHeatmap2D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHeatmap2D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
	
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
	public CmdGlObjectHeatmap2D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HEATMAP2D;
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
	public void doCommandPart() throws CerberusRuntimeException {
		
		GLCanvasHeatmap2D canvas = 
			(GLCanvasHeatmap2D) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( fResolution );
		
		if ( iTargetCollectionSetId > -1 ) {
			canvas.setTargetSetId( iTargetCollectionSetId );
		}
		else 
		{
			refGeneralManager.getSingelton().logMsg( "CmdGLObjectHistogram2D no set defined!",
					LoggerType.ERROR_ONLY );
		}
		
		//canvas.setHistogramLength( iHistogramLevel );
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException {
		
		GLCanvasHeatmap2D canvas = 
			(GLCanvasHeatmap2D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
