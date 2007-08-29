/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.opengl.canvas.scatterplot.GLCanvasMinMaxScatterPlot3D;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectMinMaxScatterPlot3D 
extends ACmdCreate_GlCanvasUser {

	protected float[] fResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see cerberus.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectMinMaxScatterPlot3D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_MINMAX_SCATTERPLOT3D;
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
	public void doCommandPart() throws GeneViewRuntimeException
	{
		GLCanvasMinMaxScatterPlot3D canvas = 
			(GLCanvasMinMaxScatterPlot3D) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException
	{
		
	}
}
