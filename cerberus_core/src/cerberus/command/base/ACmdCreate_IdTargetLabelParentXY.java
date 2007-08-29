/**
 * 
 */
package cerberus.command.base;

import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttr;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * @see cerberus.command.ICommand
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
	
	/**
	 * Canvas Id used for OpenGL
	 * 
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#setParameterHandler(IParameterHandler)
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#checkOpenGLSetting()
	 */
	protected int iGLCanvasId = 0;
	 
	/**
	 * GLEventListener Id used for OpenGL
	 * 
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#setParameterHandler(IParameterHandler)
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL#checkOpenGLSetting()
	 * 
	 */
	protected int iGLEventListernerId = 0;
	
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
