package org.geneview.core.command.view.opengl;

import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.opengl.canvas.histogram.GLCanvasHistogram2D;
import org.geneview.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHistogram2D 
extends ACmdCreate_GlCanvasUser {
	
	protected float [] fResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see org.geneview.core.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	protected int iHistogramLevel = 50;
	
	protected String color;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectHistogram2D(
			final IGeneralManager refGeneralManager, 
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HISTOGRAM2D;
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
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 */
	public final void doCommand() {
		
		super.doCommand();
		
		if ( iTargetCollectionSetId > -1 ) {
			((GLCanvasHistogram2D)gLEventListener).setTargetSetId( iTargetCollectionSetId );
		}
		else 
		{
			generalManager.getSingelton().logMsg( 
					"CmdGLObjectHistogram2D no set defined!", 
					LoggerType.STATUS);
		}
		((GLCanvasHistogram2D)gLEventListener).setHistogramLength( iHistogramLevel );
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 */
	public final void undoCommand() {
		super.undoCommand();	
		
		((GLCanvasHistogram2D)gLEventListener).destroyGLCanvas();
	}
}
