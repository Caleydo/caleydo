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
import cerberus.view.gui.opengl.canvas.GLCanvasHistogram2D;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * @author java
 *
 */
public class CmdGlObjectHistogram2D 
extends ACmdCreate_GlCanvasUser
		implements ICommand
{
	
	protected int [] iResolution;
	
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
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);

		iResolution = new int [3];
		
		setAttributesHeatmapWidthHeight( refParameterHandler );
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HISTOGRAM2D;
	}

	private final void setAttributesHeatmapWidthHeight( IParameterHandler refParameterHandler ) {
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				CommandFactory.sDelimiter_CreateView_Size);
		
		int i=0;		
		for ( ; token.hasMoreTokens(); i++ ) {
			iResolution[i] = StringConversionTool.convertStringToInt( 
					token.nextToken(), 
					0 );
		}
		
		if ( i != 3 ) {
			String logMessage = "CmdGlObjectHistogram2D.setAttributesHeatmapWidthHeight() failed! 3 values are excpected, but only " +
			i + " are present";
			
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					logMessage,
					LoggerType.STATUS );
			
			throw new CerberusRuntimeException( logMessage );
		}
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
	}



	@Override
	public void doCommandPart() throws CerberusRuntimeException
	{
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( iResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
		canvas.setHistogramLength( 200 );
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException
	{
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
		
		canvas.destroy();
		canvas = null;
	}
	
	
}
