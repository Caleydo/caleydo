/**
 * 
 */
package cerberus.command.view.opengl;

import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHistogram2D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
	
	protected float [] fResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see cerberus.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	protected String color;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public CmdGlObjectHistogram2D(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler) {
		
		super(refGeneralManager, refParameterHandler);

		// fResolution = new float [3];
		
		setAttributesHeatmapWidthHeight( refParameterHandler );
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HISTOGRAM2D;
	}

	private final void setAttributesHeatmapWidthHeight( 
			IParameterHandler refParameterHandler ) {
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				CommandFactory.sDelimiter_Parser_DataItems);
		
		int iSizeTokens= token.countTokens();
		
		fResolution = 
			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
	}



	@Override
	public void doCommandPart() throws CerberusRuntimeException {
		
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( fResolution );
		
		if ( iTargetCollectionSetId > -1 ) {
			canvas.setTargetSetId( iTargetCollectionSetId );
		}
		else 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg( "CmdGLObjectHistogram2D no set defined!");
		}
		canvas.setHistogramLength( 3 );
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException {
		
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
		
		canvas.destroy();
		canvas = null;
	}
}
