/**
 * 
 */
package org.geneview.core.command.base;

import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttr;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * @see org.geneview.core.command.ICommand
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentXY 
extends ACmdCreate_IdTargetLabelParentAttr {

	/**
	 * Width of the widget.
	 */
	protected int iWidthX;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeightY;
	
//	/**
//	 * Canvas Id used for OpenGL
//	 * 
//	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#setParameterHandler(IParameterHandler)
//	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#checkOpenGLSetting()
//	 */
//	protected int iGLCanvasId = 0;
	 
	/**
	 * GLEventListener Id used for OpenGL
	 * 
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#setParameterHandler(IParameterHandler)
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#checkOpenGLSetting()
	 * 
	 */
	protected int iGlForwarderId = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabelParentXY(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}	
	
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);			
		
		StringTokenizer token = new StringTokenizer(
				sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		refParameterHandler.setValueAndTypeAndDefault("iWidthX",
				token.nextToken(), 
				ParameterHandlerType.INT,
				"-1" );
		
		refParameterHandler.setValueAndTypeAndDefault("iHeightY",
				token.nextToken(), 
				ParameterHandlerType.INT,
				"-1" );
		
		iWidthX = refParameterHandler.getValueInt("iWidthX");
		iHeightY = refParameterHandler.getValueInt("iHeightY");
	}
}
