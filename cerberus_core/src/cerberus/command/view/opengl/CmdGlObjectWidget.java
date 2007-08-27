/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.widgets.GLCanvasWidget;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectWidget 
extends ACmdCreate_GlCanvasUser
implements ICommand
{

	protected float[] fColorSpots = { 1.0f, 0, 0};
	
	protected float[] fResolution;
	
	protected String sGridDetails = "";
	
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
	public CmdGlObjectWidget(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_WIDGET;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				this.sDetail, 
				-1 );
		
		fResolution = StringConversionTool.convertStringToFloatArray( 
				sAttribute3,
				14 );
		
		String sColor = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		sGridDetails = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
		
		if ( sColor != null ) 
		{
			fColorSpots = StringConversionTool.convertStringToFloatArrayVariableLength(
					sColor );
		}
	}

	@Override
	public void doCommandPart() throws GeneViewRuntimeException
	{
		GLCanvasWidget canvas = 
			(GLCanvasWidget) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		canvas.setTargetSetId( iTargetCollectionSetId );
		canvas.setcolorDataPoints(fColorSpots);
		
//		if ( sGridDetails.startsWith("no_grid") ) {
//			canvas.setShowGrid(false);
//		}
		
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException
	{
		
	}

}
