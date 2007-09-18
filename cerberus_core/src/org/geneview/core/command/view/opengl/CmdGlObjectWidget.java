/**
 * 
 */
package org.geneview.core.command.view.opengl;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.opengl.canvas.widgets.GLCanvasWidget;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectWidget 
extends ACmdCreate_GlCanvasUser {

	protected float[] fColorSpots = { 1.0f, 0, 0};
	
	protected float[] fResolution;
	
	protected String sGridDetails = "";
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see org.geneview.core.data.collection.ISet
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
