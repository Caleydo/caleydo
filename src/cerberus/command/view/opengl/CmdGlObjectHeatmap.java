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
import cerberus.view.gui.opengl.canvas.GLCanvasHeatmap;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * @author java
 *
 */
public class CmdGlObjectHeatmap 
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
	public CmdGlObjectHeatmap(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);

		iResolution = new int [3];
		
		setAttributesHeatmapWidthHeight( refParameterHandler );
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HEATMAP;
	}

	private final void setAttributesHeatmapWidthHeight( IParameterHandler refParameterHandler ) {
		
		//super.setAttributesBaseParent( refParameterHandler );
		
		
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
			String logMessage = "CmdGlObjectHeatmap.setAttributesHeatmapWidthHeight() failed! 3 values are excpected, but only " +
			i + " are present";
			
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					logMessage,
					LoggerType.STATUS.getLevel() );
			
			throw new CerberusRuntimeException( logMessage );
		}
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
	}



	@Override
	public void doCommandPart() throws CerberusRuntimeException
	{

		GLCanvasHeatmap canvas = 
			(GLCanvasHeatmap) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( iResolution );
		canvas.setTragetSetId( iTargetCollectionSetId );
		
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException
	{
		
	}
	
	
}
