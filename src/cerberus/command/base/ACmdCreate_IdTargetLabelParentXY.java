/**
 * 
 */
package cerberus.command.base;

import java.util.StringTokenizer;


import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttr;
import cerberus.manager.IGeneralManager;
//import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;

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
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParentXY(
			final IGeneralManager refGeneralManager )
	{
		super(refGeneralManager);
	}	
	
	/**
	 * 
	 * @deprecated 
	 */
	protected ACmdCreate_IdTargetLabelParentXY(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler )
	{
		super(refGeneralManager, refParameterHandler);
	}	
		
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		StringTokenizer token = new StringTokenizer(
				sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		refParameterHandler.setValueAndTypeAndDefault( 
				CommandQueueSaxType.TAG_POS_WIDTH_X.getXmlKey(),
				token.nextToken(), 
				ParameterHandlerType.INT,
				"-1" );
		
		refParameterHandler.setValueAndTypeAndDefault( 
				CommandQueueSaxType.TAG_POS_HEIGHT_Y.getXmlKey(),
				token.nextToken(), 
				ParameterHandlerType.INT,
				"-1" );
	}
	
	
	public final int getWidthX() {
		return this.iWidthX;
	}
	
	public final int getHeightX() {
		return this.iHeightY;
	}

}
