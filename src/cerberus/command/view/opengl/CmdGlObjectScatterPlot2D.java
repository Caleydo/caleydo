/**
 * 
 */
package cerberus.command.view.opengl;


import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.ILoggerManager.LoggerType;
//import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.scatterplot.GLCanvasScatterPlot2D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
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
	public CmdGlObjectScatterPlot2D(IGeneralManager refGeneralManager)
	{
		super(refGeneralManager);
		
		iResolution = new int[3];
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_SCATTERPLOT2D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				this.sDetail, 
				-1 );
		
		iResolution = StringConversionTool.convertStringToIntArray(
				refGeneralManager.getSingelton().getLoggerManager(), 
				sAttribute3,
				3 );
		
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
