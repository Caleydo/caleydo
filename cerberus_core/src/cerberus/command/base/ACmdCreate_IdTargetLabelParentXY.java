/**
 * 
 */
package cerberus.command.base;

import java.util.StringTokenizer;


import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttr;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentXY 
extends ACmdCreate_IdTargetLabelParentAttr 
implements ICommand
{

	/**
	 * Width of the widget.
	 */
	protected int iWidthX;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeightY;
	
	protected int iGLCanvasId = 0;
	 
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
		
		iGLCanvasId = refParameterHandler.getValueInt(
				CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() );
				
		iGLEventListernerId = refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_GLCANVAS_LISTENER.getXmlKey() );				
		
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
		

		if ( iGLCanvasId < 1) {
			this.refGeneralManager.getSingelton().logMsg(" tag [" + 
					CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() +
					"] is not assinged!",
					LoggerType.MINOR_ERROR_XML);
		}
		
		if ( iGLEventListernerId < 1) {
			this.refGeneralManager.getSingelton().logMsg(" tag [" + 
					CommandQueueSaxType.TAG_GLCANVAS_LISTENER.getXmlKey() +
					"] is not assinged!",
					LoggerType.MINOR_ERROR_XML);
		}
	}
}
