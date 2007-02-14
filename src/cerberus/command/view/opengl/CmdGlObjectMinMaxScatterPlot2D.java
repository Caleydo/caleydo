/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.scatterplot.GLCanvasMinMaxScatterPlot2D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectMinMaxScatterPlot2D 
extends ACmdCreate_GlCanvasUser
implements ICommand
{

	protected float[] fResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see cerberus.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public CmdGlObjectMinMaxScatterPlot2D(IGeneralManager refGeneralManager)
	{
		super(refGeneralManager);
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_MINMAX_SCATTERPLOT2D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				this.sDetail, 
				-1 );
		
		fResolution = StringConversionTool.convertStringToFloatArray( 
				sAttribute3,
				14 );
	}

	@Override
	public void doCommandPart() throws CerberusRuntimeException
	{
		GLCanvasMinMaxScatterPlot2D canvas = 
			(GLCanvasMinMaxScatterPlot2D) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( fResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
		
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException
	{
		
	}

}
