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
import cerberus.view.gui.opengl.canvas.GLCanvasScatterPlot2D;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * @author java
 *
 */
public class CmdGlObjectScatterPlot2D 
extends ACmdCreate_GlCanvasUser
		implements ICommand
{
	
	protected int[] iResolution;
	
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
	public CmdGlObjectScatterPlot2D(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		iResolution = new int[3];
		
		setAttributesHeatmapWidthHeight( refParameterHandler );
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_SCATTERPLOT2D;
	}

	private final void setAttributesHeatmapWidthHeight( IParameterHandler refParameterHandler ) {
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				this.sDetail, 
				-1 );
		
		StringTokenizer tokenize = new StringTokenizer( sAttribute3,
				CommandFactory.sDelimiter_CreateSelection_DataItems );
		
		int i=0;		
		for ( ; tokenize.hasMoreTokens(); i++ ) 
		{
			if ( i > 2 )
			{
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"attrib3='" + sAttribute3 + "' should contain 3 interger values! ignore remaining values!",
						LoggerType.VERBOSE );
			}
			
			iResolution[i] = StringConversionTool.convertStringToInt( 
				tokenize.nextToken(), 0 );					
		}
		
		if ( i != 3 ) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"attrib3='" + sAttribute3 + "' should contain 3 interger values!",
					LoggerType.MINOR_ERROR );
		}
	}



	@Override
	public void doCommandPart() throws CerberusRuntimeException
	{
		GLCanvasScatterPlot2D canvas = 
			(GLCanvasScatterPlot2D) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( iResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException
	{
		
	}
	
	
}
